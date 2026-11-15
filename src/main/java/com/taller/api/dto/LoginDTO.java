package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "El usuario es obligatorio") String usuario,
        @NotBlank(message = "La clave es obligatoria") String clave) {
}
