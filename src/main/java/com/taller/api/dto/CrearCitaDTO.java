package com.taller.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Para qué sirve: Cuerpo para agendar una cita (la fecha debe estar en el futuro).
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CrearCitaDTO(
        @NotBlank String idCliente,
        @NotBlank String placa,
        @NotNull @Future(message = "La fecha de la cita debe estar en el futuro") LocalDateTime fechaHora,
        String motivo,
        String canal) {
}
