package com.taller.api.controlador;

import com.taller.api.dto.*;
import com.taller.api.mapeador.MapeadorApi;
import com.taller.aplicacion.servicio.DiagnosticoService;
import com.taller.aplicacion.servicio.OrdenTrabajoService;
import com.taller.dominio.modelo.CanalAprobacion;
import com.taller.dominio.modelo.Diagnostico;
import com.taller.dominio.modelo.EstadoOrden;
import com.taller.dominio.modelo.OrdenTrabajo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Ordenes de trabajo.
 *
 * <p>Las transiciones no son endpoints con verbo en la URL: se modelan como un
 * subrecurso de estado, para que la URL siga nombrando un recurso y la maquina
 * de estados siga siendo quien valida.
 */
@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "Ordenes de trabajo")
public class OrdenTrabajoControlador {

    private final OrdenTrabajoService ordenes;
    private final DiagnosticoService diagnosticos;

    public OrdenTrabajoControlador(OrdenTrabajoService ordenes, DiagnosticoService diagnosticos) {
        this.ordenes = ordenes;
        this.diagnosticos = diagnosticos;
    }

    @GetMapping
    @Operation(summary = "Tablero de ordenes, filtrable por estado o placa")
    public List<OrdenResumenDTO> listar(@RequestParam(required = false) EstadoOrden estado,
                                        @RequestParam(required = false) String placa) {
        List<OrdenTrabajo> resultado;
        if (placa != null) {
            resultado = ordenes.listarPorPlaca(placa);
        } else if (estado != null) {
            resultado = ordenes.listarPorEstado(estado);
        } else {
            resultado = ordenes.listarPorEstado(EstadoOrden.EN_REPARACION);
        }
        return resultado.stream().map(MapeadorApi::aResumen).toList();
    }

    @PostMapping
    @Operation(summary = "Crea una orden al recibir el vehiculo")
    public ResponseEntity<OrdenDetalleDTO> crear(@Valid @RequestBody CrearOrdenDTO dto,
                                                 @RequestHeader(value = "X-Usuario", defaultValue = "recepcion")
                                                 String usuario) {
        OrdenTrabajo orden = ordenes.crear(dto.placa(), dto.kilometraje(), dto.idCita(), usuario);
        return ResponseEntity.created(URI.create("/api/v1/ordenes/" + orden.getId()))
                .body(MapeadorApi.aDetalle(orden));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una orden")
    public OrdenDetalleDTO obtener(@PathVariable String id) {
        return MapeadorApi.aDetalle(ordenes.obtener(id));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Linea de tiempo de los estados")
    public List<OrdenDetalleDTO.HitoDTO> historial(@PathVariable String id) {
        return ordenes.historial(id).stream()
                .map(h -> new OrdenDetalleDTO.HitoDTO(h.estado().name(), h.fecha(), h.usuario()))
                .toList();
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Transiciona la orden; un salto ilegal devuelve 409")
    public OrdenDetalleDTO cambiarEstado(@PathVariable String id,
                                         @Valid @RequestBody CambiarEstadoDTO dto,
                                         @RequestHeader(value = "X-Usuario", defaultValue = "recepcion")
                                         String usuario) {
        EstadoOrden destino = EstadoOrden.valueOf(dto.estado());
        OrdenTrabajo orden = switch (destino) {
            case EN_DIAGNOSTICO -> ordenes.registrarDiagnostico(id,
                    diagnosticos.registroManual(List.of(), "Inicio de diagnostico"), usuario);
            case COTIZADA -> ordenes.emitirCotizacion(id, 48, usuario);
            case APROBADA -> ordenes.aprobarCotizacion(id,
                    dto.canal() == null ? CanalAprobacion.PRESENCIAL : CanalAprobacion.valueOf(dto.canal()),
                    usuario);
            case RECHAZADA -> ordenes.rechazarCotizacion(id, dto.motivo(), usuario);
            case EN_REPARACION -> ordenes.asignarMecanico(id, dto.idMecanico(), usuario);
            case EN_ESPERA_REPUESTO -> ordenes.reanudar(id, usuario);
            case CERRADA -> ordenes.cerrar(id, usuario);
            case ENTREGADA -> ordenes.entregar(id, usuario);
            default -> throw new IllegalArgumentException(
                    "El estado %s no se alcanza por este endpoint".formatted(destino));
        };
        return MapeadorApi.aDetalle(orden);
    }

    @PostMapping("/{id}/diagnosticos")
    @Operation(summary = "Registra un diagnostico, desde el escaner o manual")
    public ResponseEntity<OrdenDetalleDTO> registrarDiagnostico(
            @PathVariable String id,
            @RequestBody RegistrarDiagnosticoDTO dto,
            @RequestHeader(value = "X-Usuario", defaultValue = "mecanico") String usuario) {
        OrdenTrabajo orden = ordenes.obtener(id);
        Diagnostico diagnostico = null;
        if (dto.usarEscaner()) {
            diagnostico = diagnosticos.intentarEscaneo(orden.getVehiculo().placa());
        }
        if (diagnostico == null) {
            diagnostico = diagnosticos.registroManual(dto.codigosFalla(), dto.observaciones());
        }
        OrdenTrabajo actualizada = ordenes.registrarDiagnostico(id, diagnostico, usuario);
        return ResponseEntity.status(201).body(MapeadorApi.aDetalle(actualizada));
    }

    @PostMapping("/{id}/servicios")
    @Operation(summary = "Agrega un servicio del catalogo")
    public ResponseEntity<OrdenDetalleDTO> agregarServicio(@PathVariable String id,
                                                           @RequestParam String codigo) {
        return ResponseEntity.status(201).body(MapeadorApi.aDetalle(ordenes.agregarServicio(id, codigo)));
    }

    @PostMapping("/{id}/repuestos")
    @Operation(summary = "Registra consumo de repuesto; sin stock devuelve 409 y bloquea la orden")
    public ResponseEntity<OrdenDetalleDTO> registrarRepuesto(
            @PathVariable String id,
            @Valid @RequestBody ConsumirRepuestoDTO dto,
            @RequestHeader(value = "X-Usuario", defaultValue = "mecanico") String usuario) {
        OrdenTrabajo orden = ordenes.registrarRepuesto(id, dto.codigo(), dto.cantidad(), usuario);
        return ResponseEntity.status(201).body(MapeadorApi.aDetalle(orden));
    }
}
