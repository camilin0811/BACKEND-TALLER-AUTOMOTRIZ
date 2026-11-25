package com.taller.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Nueva cantidad de un repuesto en una orden de compra en borrador. */
/**
 * Para qué sirve: Cuerpo para cambiar la cantidad de un ítem de una orden de compra (1 a 10.000).
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record AjustarCantidadCompraDTO(
        @Min(value = 1, message = "La cantidad debe ser mayor que cero")
        @Max(value = 10000, message = "La cantidad no puede pasar de 10.000 unidades")
        int cantidad) {
}
