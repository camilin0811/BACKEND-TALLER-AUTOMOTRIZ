package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Datos de la consulta publica del cliente.
 *
 * <p>Viaja en el cuerpo de un POST y no en la URL: la placa y el documento
 * no deben quedar en los registros de acceso, en el historial del navegador
 * ni en las cabeceras Referer.
 *
 * <p>Las reglas son las mismas que valida el formulario del portal. El
 * servidor las repite porque el formulario se puede saltar.
 *
 * <p>Para qué sirve: Placa y documento que el cliente escribe en el portal público.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record ConsultaPublicaDTO(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "[A-Za-z0-9]{3,8}",
                 message = "La placa solo admite letras y números (entre 3 y 8)")
        String placa,

        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "\\d{5,15}",
                 message = "El documento solo admite dígitos (entre 5 y 15)")
        String documento) {
}
