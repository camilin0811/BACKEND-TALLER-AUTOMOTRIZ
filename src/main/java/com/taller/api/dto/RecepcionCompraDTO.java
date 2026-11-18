package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Mercancia recibida de una orden de compra enviada, total o parcial. */
public record RecepcionCompraDTO(
        @NotBlank(message = "Indique el código del repuesto recibido")
        String codigo,
        @Min(value = 1, message = "La cantidad recibida debe ser mayor que cero")
        int cantidad) {
}
