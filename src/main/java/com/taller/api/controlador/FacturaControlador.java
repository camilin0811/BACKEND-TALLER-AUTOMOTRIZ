package com.taller.api.controlador;

import com.taller.api.dto.RegistrarPagoDTO;
import com.taller.aplicacion.servicio.FacturacionService;
import com.taller.dominio.modelo.Factura;
import com.taller.dominio.modelo.MetodoPago;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
@Tag(name = "Facturacion")
public class FacturaControlador {

    private final FacturacionService facturacion;

    public FacturaControlador(FacturacionService facturacion) {
        this.facturacion = facturacion;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta una factura con su CUFE")
    public Factura obtener(@PathVariable String id) {
        return facturacion.obtener(id);
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Facturas que no se pudieron enviar a la DIAN")
    public List<Factura> pendientes() {
        return facturacion.listarPendientesDeEnvio();
    }

    @PostMapping("/{id}/pagos")
    @Operation(summary = "Registra un pago; si excede el saldo devuelve 422 con el saldo real")
    public ResponseEntity<Factura> registrarPago(@PathVariable String id,
                                                 @Valid @RequestBody RegistrarPagoDTO dto) {
        Factura factura = facturacion.registrarPago(id, dto.monto(),
                MetodoPago.valueOf(dto.metodo()), dto.referencia());
        return ResponseEntity.status(201).body(factura);
    }

    @PostMapping("/{id}/reenvio-dian")
    @Operation(summary = "Reintenta la validacion ante la DIAN")
    public ResponseEntity<Factura> reenviar(@PathVariable String id) {
        return ResponseEntity.accepted().body(facturacion.reintentarEnvio(id));
    }
}
