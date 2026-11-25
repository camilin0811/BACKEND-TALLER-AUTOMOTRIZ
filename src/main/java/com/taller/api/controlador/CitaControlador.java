package com.taller.api.controlador;

import com.taller.api.dto.CrearCitaDTO;
import com.taller.aplicacion.servicio.CitaService;
import com.taller.dominio.modelo.Cita;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Para qué sirve: Agenda de citas: citas del día, disponibilidad, agendar y confirmar o cancelar
 * (/api/v1/citas).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): entra y sale por objetos de transferencia (CrearCitaDTO),
 *     nunca expone las entidades del dominio.
 */
@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas")
public class CitaControlador {

    private final CitaService citas;

    public CitaControlador(CitaService citas) {
        this.citas = citas;
    }

    @GetMapping
    @Operation(summary = "Citas de un dia")
    public List<Cita> listar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citas.listarPorFecha(fecha);
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Indica si hay cupo y, si no, propone alternativas")
    public Map<String, Object> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        boolean hay = citas.hayDisponibilidad(fechaHora);
        return Map.of("disponible", hay, "alternativas", hay ? List.of() : citas.alternativas(fechaHora));
    }

    @PostMapping
    @Operation(summary = "Solicita una cita")
    public ResponseEntity<Cita> agendar(@Valid @RequestBody CrearCitaDTO dto) {
        Cita cita = citas.agendar(dto.idCliente(), dto.placa(), dto.fechaHora(), dto.motivo(), dto.canal());
        return ResponseEntity.created(URI.create("/api/v1/citas/" + cita.getId())).body(cita);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Confirma o cancela una cita")
    public Cita cambiarEstado(@PathVariable String id, @RequestParam String estado) {
        return "CONFIRMADA".equalsIgnoreCase(estado) ? citas.confirmar(id) : citas.cancelar(id);
    }
}
