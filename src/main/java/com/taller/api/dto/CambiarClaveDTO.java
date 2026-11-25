package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Para qué sirve: Cuerpo para que el usuario cambie su clave (mínimo 8 caracteres).
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CambiarClaveDTO(
        @NotBlank String claveActual,
        @NotBlank @Size(min = 8, message = "La clave debe tener al menos 8 caracteres") String claveNueva) {
}
