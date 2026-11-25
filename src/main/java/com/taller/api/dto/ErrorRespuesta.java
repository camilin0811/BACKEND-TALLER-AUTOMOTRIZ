package com.taller.api.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Forma unica de los errores de la API.
 *
 * <p>El campo {@code codigo} es estable y apto para que el cliente reaccione
 * programaticamente; el {@code mensaje} es para humanos y puede cambiar.
 *
 * <p>Para qué sirve: Forma única de todos los errores de la API: estado HTTP, código estable, mensaje,
 * ruta y detalle.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record ErrorRespuesta(Instant timestamp,
                             int status,
                             String error,
                             String codigo,
                             String mensaje,
                             String ruta,
                             String correlacionId,
                             Map<String, Object> detalle) {
}
