package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ConsumirRepuestoDTO(
        @NotBlank String codigo,
        @Min(value = 1, message = "La cantidad debe ser mayor que cero") int cantidad) {
}
