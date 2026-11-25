package com.taller.api.dto;

/**
 * Para qué sirve: Respuesta del login: el JWT, su vigencia y los datos del usuario.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record TokenDTO(String token, String tipo, long expiraEnSegundos,
                       String usuario, String rol, String nombre) {
}
