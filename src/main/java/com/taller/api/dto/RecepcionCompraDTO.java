package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Mercancia recibida de una orden de compra enviada, total o parcial. */
/**
 * Para qué sirve: Cuerpo para registrar la llegada de repuestos de una orden de compra.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record RecepcionCompraDTO(
        @NotBlank(message = "Indique el código del repuesto recibido")
        String codigo,
        @Min(value = 1, message = "La cantidad recibida debe ser mayor que cero")
        int cantidad) {
}
