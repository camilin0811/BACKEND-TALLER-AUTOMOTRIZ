package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarEstadoDTO(
        @NotBlank(message = "El estado destino es obligatorio") String estado,
        String idMecanico,
        String motivo,
        String canal) {
}
