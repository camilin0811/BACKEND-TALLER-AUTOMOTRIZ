package com.taller.api.controlador;

import com.taller.api.dto.CrearVehiculoDTO;
import com.taller.api.mapeador.MapeadorApi;
import com.taller.aplicacion.servicio.VehiculoService;
import com.taller.dominio.modelo.Vehiculo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Para qué sirve: Registro y consulta de vehículos y actualización del kilometraje
 * (/api/v1/vehiculos).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): entra y sale por objetos de transferencia (CrearVehiculoDTO),
 *     nunca expone las entidades del dominio.
 */
@RestController
@RequestMapping("/api/v1/vehiculos")
@Tag(name = "Vehiculos")
public class VehiculoControlador {

    private final VehiculoService vehiculos;

    public VehiculoControlador(VehiculoService vehiculos) {
        this.vehiculos = vehiculos;
    }

    @PostMapping
    @Operation(summary = "Registra un automovil o una motocicleta")
    public ResponseEntity<Vehiculo> registrar(@Valid @RequestBody CrearVehiculoDTO dto) {
        Vehiculo creado = vehiculos.registrar(MapeadorApi.aVehiculo(dto));
        return ResponseEntity.created(URI.create("/api/v1/vehiculos/" + creado.getPlaca())).body(creado);
    }

    @GetMapping("/{placa}")
    @Operation(summary = "Consulta un vehículo por placa")
    public Vehiculo obtener(@PathVariable String placa) {
        return vehiculos.obtenerPorPlaca(placa);
    }

    @PatchMapping("/{placa}/kilometraje")
    @Operation(summary = "Actualiza el kilometraje; nunca puede decrecer")
    public Vehiculo actualizarKilometraje(@PathVariable String placa, @RequestParam int valor) {
        return vehiculos.actualizarKilometraje(placa, valor);
    }
}
