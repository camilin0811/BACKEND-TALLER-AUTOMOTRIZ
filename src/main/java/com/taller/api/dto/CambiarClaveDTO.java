package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarClaveDTO(
        @NotBlank String claveActual,
        @NotBlank @Size(min = 8, message = "La clave debe tener al menos 8 caracteres") String claveNueva) {
}
