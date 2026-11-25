package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Para qué sirve: Cuerpo para dar de alta un repuesto en el almacén.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CrearRepuestoDTO(
        @NotBlank String codigo,
        @NotBlank String nombre,
        @NotNull @Positive BigDecimal precio,
        @Min(0) int stock,
        @Min(0) int stockMinimo) {
}
