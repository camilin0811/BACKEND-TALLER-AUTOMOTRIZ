package com.taller.api.controlador;

import com.taller.api.dto.CrearClienteDTO;
import com.taller.aplicacion.servicio.ClienteService;
import com.taller.aplicacion.servicio.VehiculoService;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.modelo.Vehiculo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Para qué sirve: Registro y consulta de clientes y de sus vehículos (/api/v1/clientes).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): entra y sale por objetos de transferencia (CrearClienteDTO),
 *     nunca expone las entidades del dominio.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes")
public class ClienteControlador {

    private final ClienteService clientes;
    private final VehiculoService vehiculos;

    public ClienteControlador(ClienteService clientes, VehiculoService vehiculos) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
    }

    @GetMapping
    @Operation(summary = "Lista los clientes registrados")
    public List<Cliente> listar() {
        return clientes.listar();
    }

    @PostMapping
    @Operation(summary = "Registra un cliente")
    public ResponseEntity<Cliente> registrar(@Valid @RequestBody CrearClienteDTO dto) {
        Cliente creado = clientes.registrar(dto.nombre(), dto.documento(), dto.telefono(),
                dto.email(), dto.direccion());
        return ResponseEntity.created(URI.create("/api/v1/clientes/" + creado.getId())).body(creado);
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Busca un cliente por su documento; 404 si no existe")
    public Cliente buscarPorDocumento(@PathVariable String documento) {
        return clientes.obtenerPorDocumento(documento.trim());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un cliente")
    public Cliente obtener(@PathVariable String id) {
        return clientes.obtener(id);
    }

    @GetMapping("/{id}/vehiculos")
    @Operation(summary = "Vehiculos de un cliente")
    public List<Vehiculo> vehiculosDe(@PathVariable String id) {
        return vehiculos.listarPorCliente(id);
    }
}
