package com.taller.api.controlador;

import com.taller.api.dto.ConsultaPublicaDTO;
import com.taller.api.dto.ConsultaPublicaRespuestaDTO;
import com.taller.api.mapeador.MapeadorApi;
import com.taller.aplicacion.servicio.ClienteService;
import com.taller.aplicacion.servicio.OrdenTrabajoService;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.modelo.OrdenTrabajo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * Consulta publica del cliente.
 *
 * <p>Exige placa <strong>y</strong> documento. Pedir solo la placa seria un
 * hueco de privacidad: las placas se leen en la calle, y cualquiera podria ver
 * cuanto paga un tercero por su reparacion. La historia HU-18 ya lo planteaba
 * asi: el cliente entra con su documento.
 *
 * <p>Es POST con cuerpo JSON, no GET con parametros: asi la placa y el
 * documento no quedan en los registros de acceso ni en el historial. Y esta
 * limitada por IP ({@code LimiteConsultaPublica}) para que nadie pueda
 * adivinar documentos probando por fuerza bruta.
 *
 * <p>Para qué sirve: Portal público: el cliente consulta su orden con placa y documento, sin iniciar
 * sesión (/api/v1/publico).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): ConsultaPublicaRespuestaDTO expone solo lo que el cliente
 *     puede ver de su propia orden.
 */
@RestController
@RequestMapping("/api/v1/publico")
@Tag(name = "Consulta publica")
public class ConsultaPublicaControlador {

    private final OrdenTrabajoService ordenes;
    private final ClienteService clientes;

    public ConsultaPublicaControlador(OrdenTrabajoService ordenes, ClienteService clientes) {
        this.ordenes = ordenes;
        this.clientes = clientes;
    }

    @PostMapping("/orden")
    @Operation(summary = "Estado de la orden más reciente de un vehículo, verificando el documento del cliente")
    public ConsultaPublicaRespuestaDTO consultar(@Valid @RequestBody ConsultaPublicaDTO consulta) {
        String placa = consulta.placa().toUpperCase();

        // La misma respuesta si el documento no existe, si la placa no existe
        // o si la placa no es de ese cliente: responder distinto permitiria
        // descubrir quien es cliente del taller, o de quien es un vehiculo,
        // probando documentos. Y el mensaje no repite los datos enviados.
        Cliente cliente;
        try {
            cliente = clientes.obtenerPorDocumento(consulta.documento());
        } catch (RecursoNoEncontradoException e) {
            throw sinResultado();
        }

        List<OrdenTrabajo> suyas = ordenes.listarPorPlaca(placa).stream()
                .filter(o -> o.getCliente() != null && cliente.getId().equals(o.getCliente().id()))
                .sorted(Comparator.comparing(OrdenTrabajo::getFechaIngreso).reversed())
                .toList();
        if (suyas.isEmpty()) {
            throw sinResultado();
        }

        // La que le importa al cliente es la que sigue en curso; si ya
        // retiro el vehiculo de todas, la ultima.
        OrdenTrabajo principal = suyas.stream()
                .filter(o -> o.getEstado().vehiculoEnTaller())
                .findFirst()
                .orElse(suyas.get(0));

        List<ConsultaPublicaRespuestaDTO.OrdenHistorial> historial = suyas.stream()
                .map(o -> new ConsultaPublicaRespuestaDTO.OrdenHistorial(o.getNumero(),
                        o.getEstado().name(), o.getFechaIngreso(), o.getFechaEntrega(), o.getTotal()))
                .toList();

        return new ConsultaPublicaRespuestaDTO(MapeadorApi.aDetallePublico(principal), historial);
    }

    private static RecursoNoEncontradoException sinResultado() {
        return RecursoNoEncontradoException.conMensaje(
                "No encontramos órdenes con esa placa y ese documento.");
    }
}
