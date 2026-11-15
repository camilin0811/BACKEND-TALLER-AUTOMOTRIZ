package com.taller.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CrearCitaDTO(
        @NotBlank String idCliente,
        @NotBlank String placa,
        @NotNull @Future(message = "La fecha de la cita debe estar en el futuro") LocalDateTime fechaHora,
        String motivo,
        String canal) {
}
