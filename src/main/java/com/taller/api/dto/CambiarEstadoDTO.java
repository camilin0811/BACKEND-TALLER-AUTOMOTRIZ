package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Para qué sirve: Cuerpo para mover la orden a otro estado, con los datos que algunos destinos
 * exigen (mecánico, motivo o canal).
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CambiarEstadoDTO(
        @NotBlank(message = "El estado destino es obligatorio") String estado,
        String idMecanico,
        String motivo,
        String canal) {
}
