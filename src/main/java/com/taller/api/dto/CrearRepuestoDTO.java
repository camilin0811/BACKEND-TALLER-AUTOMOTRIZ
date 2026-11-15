package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CrearRepuestoDTO(
        @NotBlank String codigo,
        @NotBlank String nombre,
        @NotNull @Positive BigDecimal precio,
        @Min(0) int stock,
        @Min(0) int stockMinimo) {
}
