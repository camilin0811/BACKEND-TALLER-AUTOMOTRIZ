package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Mecanico que se asigna a una orden en trabajo. */
/**
 * Para qué sirve: Cuerpo para asignar o cambiar el mecánico de una orden.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record AsignarMecanicoDTO(@NotBlank(message = "Elija un mecánico") String idMecanico) {
}
