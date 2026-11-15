package com.taller.api.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Forma unica de los errores de la API.
 *
 * <p>El campo {@code codigo} es estable y apto para que el cliente reaccione
 * programaticamente; el {@code mensaje} es para humanos y puede cambiar.
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
