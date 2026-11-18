package com.taller.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Respuesta de la consulta publica: la orden que le importa al cliente y el
 * historial del vehiculo.
 *
 * <p>{@code orden} es la orden activa mas reciente; si el vehiculo no tiene
 * ninguna en curso, la ultima. {@code historial} trae todas sus ordenes, de
 * la mas reciente a la mas antigua, con lo minimo para reconocerlas: no
 * expone identificadores internos.
 */
public record ConsultaPublicaRespuestaDTO(OrdenDetalleDTO orden, List<OrdenHistorial> historial) {

    public record OrdenHistorial(String numero,
                                 String estado,
                                 Instant fechaIngreso,
                                 Instant fechaEntrega,
                                 BigDecimal total) {
    }
}
