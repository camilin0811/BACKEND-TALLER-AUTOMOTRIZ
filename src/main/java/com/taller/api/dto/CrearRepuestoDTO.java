package com.taller.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Para qué sirve: Cuerpo para dar de alta un repuesto en el almacén.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CrearRepuestoDTO(
        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "[A-Za-z0-9-]{3,20}",
                message = "El código solo admite letras, números y guiones (entre 3 y 20)")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombre,
        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que cero")
        @DecimalMax(value = "100000000", message = "El precio no puede pasar de 100.000.000")
        BigDecimal precio,
        @Min(value = 0, message = "El stock inicial no puede ser negativo")
        @Max(value = 100000, message = "El stock inicial no puede pasar de 100.000 unidades")
        int stock,
        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        @Max(value = 100000, message = "El stock mínimo no puede pasar de 100.000 unidades")
        int stockMinimo) {
}
