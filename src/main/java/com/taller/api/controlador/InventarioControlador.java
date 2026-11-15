package com.taller.api.controlador;

import com.taller.api.dto.CrearRepuestoDTO;
import com.taller.aplicacion.servicio.InventarioService;
import com.taller.dominio.modelo.MovimientoInventario;
import com.taller.dominio.modelo.OrdenCompra;
import com.taller.dominio.modelo.Repuesto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Inventario")
public class InventarioControlador {

    private final InventarioService inventario;

    public InventarioControlador(InventarioService inventario) {
        this.inventario = inventario;
    }

    @GetMapping("/repuestos")
    @Operation(summary = "Lista repuestos; con bajoMinimo=true solo los que requieren reposicion")
    public List<Repuesto> listar(@RequestParam(defaultValue = "false") boolean bajoMinimo) {
        return bajoMinimo ? inventario.listarBajoMinimo() : inventario.listar();
    }

    @PostMapping("/repuestos")
    @Operation(summary = "Crea un repuesto")
    public ResponseEntity<Repuesto> crear(@Valid @RequestBody CrearRepuestoDTO dto) {
        Repuesto creado = inventario.crear(Repuesto.nuevo(dto.codigo(), dto.nombre(), dto.precio(),
                dto.stock(), dto.stockMinimo()));
        return ResponseEntity.created(URI.create("/api/v1/repuestos/" + creado.getCodigo())).body(creado);
    }

    @GetMapping("/repuestos/{codigo}")
    @Operation(summary = "Consulta un repuesto con su stock actual")
    public Repuesto obtener(@PathVariable String codigo) {
        return inventario.obtener(codigo);
    }

    @GetMapping("/repuestos/{codigo}/movimientos")
    @Operation(summary = "Historial de movimientos del repuesto")
    public List<MovimientoInventario> movimientos(@PathVariable String codigo) {
        return inventario.movimientosDe(codigo);
    }

    @GetMapping("/ordenes-compra")
    @Operation(summary = "Ordenes de compra, incluidas las generadas por reposicion automatica")
    public List<OrdenCompra> ordenesCompra() {
        return inventario.listarOrdenesCompra();
    }

    @PostMapping("/ordenes-compra/{id}/recepcion")
    @Operation(summary = "Registra la recepcion total o parcial de un repuesto")
    public ResponseEntity<OrdenCompra> recibir(@PathVariable String id,
                                               @RequestParam String codigo,
                                               @RequestParam int cantidad) {
        return ResponseEntity.status(201).body(inventario.registrarRecepcion(id, codigo, cantidad));
    }
}
