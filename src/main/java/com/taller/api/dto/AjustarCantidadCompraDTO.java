package com.taller.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Nueva cantidad de un repuesto en una orden de compra en borrador. */
public record AjustarCantidadCompraDTO(
        @Min(value = 1, message = "La cantidad debe ser mayor que cero")
        @Max(value = 10000, message = "La cantidad no puede pasar de 10.000 unidades")
        int cantidad) {
}
