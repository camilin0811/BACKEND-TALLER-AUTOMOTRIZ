package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Para qué sirve: Cuerpo para descontar un repuesto del inventario y cargarlo a la orden.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record ConsumirRepuestoDTO(
        @NotBlank String codigo,
        @Min(value = 1, message = "La cantidad debe ser mayor que cero") int cantidad) {
}
