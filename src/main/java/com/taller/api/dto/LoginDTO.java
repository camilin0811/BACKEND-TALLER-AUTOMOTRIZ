package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Para qué sirve: Usuario y clave del inicio de sesión.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record LoginDTO(
        @NotBlank(message = "El usuario es obligatorio") String usuario,
        @NotBlank(message = "La clave es obligatoria") String clave) {
}
