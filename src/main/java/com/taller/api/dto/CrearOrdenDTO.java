package com.taller.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Recepcion de un vehiculo: abre una orden de trabajo en estado Recibida. */
/**
 * Para qué sirve: Cuerpo para abrir una orden de trabajo al recibir un vehículo.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CrearOrdenDTO(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "[A-Za-z0-9]{3,8}", message = "La placa solo admite letras y números (entre 3 y 8)")
        String placa,
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        @Max(value = 2_000_000, message = "El kilometraje no puede pasar de 2.000.000 km")
        int kilometraje,
        String idCita,
        @NotBlank(message = "Describa el motivo o los síntomas que reporta el cliente")
        @Size(min = 5, max = 500, message = "El motivo debe tener entre 5 y 500 caracteres")
        String motivo) {
}
