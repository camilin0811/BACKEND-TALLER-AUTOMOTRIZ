package com.taller.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

/** Vista compacta para el tablero de recepcion. */
/**
 * Para qué sirve: Fila del tablero de órdenes: solo lo necesario para listarlas.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record OrdenResumenDTO(String id,
                              String numero,
                              String estado,
                              String placa,
                              String tipoVehiculo,
                              String descripcionVehiculo,
                              String cliente,
                              String mecanico,
                              BigDecimal total,
                              Instant fechaIngreso) {
}
