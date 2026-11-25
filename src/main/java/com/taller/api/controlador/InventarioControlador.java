package com.taller.api.controlador;

import com.taller.api.dto.AjustarCantidadCompraDTO;
import com.taller.api.dto.CrearRepuestoDTO;
import com.taller.api.dto.RecepcionCompraDTO;
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

/**
 * Para qué sirve: Repuestos, movimientos de inventario y órdenes de compra (/api/v1/repuestos y
 * /api/v1/ordenes-compra).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): entra y sale por objetos de transferencia (CrearRepuestoDTO,
 *     AjustarCantidadCompraDTO, RecepcionCompraDTO), nunca expone las entidades del dominio.
 */
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

    /** Solo administrador: corrige la cantidad sugerida antes de enviar. */
    @PatchMapping("/ordenes-compra/{id}/items/{codigo}")
    @Operation(summary = "Ajusta la cantidad de un repuesto en una orden de compra en borrador")
    public OrdenCompra ajustarCantidad(@PathVariable String id, @PathVariable String codigo,
                                       @Valid @RequestBody AjustarCantidadCompraDTO dto) {
        return inventario.ajustarCantidad(id, codigo, dto.cantidad());
    }

    /** Solo administrador: el pedido sale al proveedor. */
    @PostMapping("/ordenes-compra/{id}/envio")
    @Operation(summary = "Envía al proveedor una orden de compra en borrador")
    public OrdenCompra enviar(@PathVariable String id) {
        return inventario.enviar(id);
    }

    /** Administrador o recepcionista: registra lo que llego y suma el stock. */
    @PostMapping("/ordenes-compra/{id}/recepcion")
    @Operation(summary = "Registra la recepción total o parcial de un repuesto de una orden enviada")
    public OrdenCompra recibir(@PathVariable String id, @Valid @RequestBody RecepcionCompraDTO dto) {
        return inventario.registrarRecepcion(id, dto.codigo(), dto.cantidad());
    }
}
