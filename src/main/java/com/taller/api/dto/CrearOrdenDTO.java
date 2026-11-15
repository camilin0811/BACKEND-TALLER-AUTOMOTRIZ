package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CrearOrdenDTO(
        @NotBlank(message = "La placa es obligatoria") String placa,
        @Min(value = 0, message = "El kilometraje no puede ser negativo") int kilometraje,
        String idCita,
        String motivo) {
}
