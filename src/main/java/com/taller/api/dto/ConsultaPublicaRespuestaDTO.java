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
 *
 * <p>Para qué sirve: Lo que el portal público muestra al cliente: solo datos de su propia orden.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record ConsultaPublicaRespuestaDTO(OrdenDetalleDTO orden, List<OrdenHistorial> historial) {

    public record OrdenHistorial(String numero,
                                 String estado,
                                 Instant fechaIngreso,
                                 Instant fechaEntrega,
                                 BigDecimal total) {
    }
}
