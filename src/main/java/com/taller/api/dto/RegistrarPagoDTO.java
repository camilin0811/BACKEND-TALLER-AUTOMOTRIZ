package com.taller.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Para qué sirve: Cuerpo para registrar un pago de una factura.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record RegistrarPagoDTO(
        @NotNull @Positive(message = "El monto debe ser mayor que cero") BigDecimal monto,
        @Pattern(regexp = "EFECTIVO|TARJETA|TRANSFERENCIA|PASARELA") String metodo,
        String referencia) {
}
