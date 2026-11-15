package com.taller.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RegistrarPagoDTO(
        @NotNull @Positive(message = "El monto debe ser mayor que cero") BigDecimal monto,
        @Pattern(regexp = "EFECTIVO|TARJETA|TRANSFERENCIA|PASARELA") String metodo,
        String referencia) {
}
