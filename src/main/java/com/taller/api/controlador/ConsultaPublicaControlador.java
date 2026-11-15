package com.taller.api.controlador;

import com.taller.api.dto.OrdenDetalleDTO;
import com.taller.api.mapeador.MapeadorApi;
import com.taller.aplicacion.servicio.ClienteService;
import com.taller.aplicacion.servicio.OrdenTrabajoService;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.modelo.OrdenTrabajo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/orden")
    @Operation(summary = "Estado de la orden mas reciente de un vehiculo, verificando el documento del cliente")
    public OrdenDetalleDTO consultar(@RequestParam String placa, @RequestParam String documento) {
        Cliente cliente = clientes.obtenerPorDocumento(documento.trim());

        List<OrdenTrabajo> delVehiculo = ordenes.listarPorPlaca(placa.trim().toUpperCase());
        OrdenTrabajo suya = delVehiculo.stream()
                .filter(o -> o.getCliente() != null && cliente.getId().equals(o.getCliente().id()))
                .max(Comparator.comparing(OrdenTrabajo::getFechaIngreso))
                // El mismo error tanto si la placa no existe como si no es de
                // este cliente: responder distinto permitiria descubrir a quien
                // pertenece un vehiculo probando documentos.
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "orden para la placa y documento indicados", placa));

        return MapeadorApi.aDetalle(suya);
    }
}
