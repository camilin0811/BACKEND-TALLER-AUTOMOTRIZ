package com.taller.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

/** Vista compacta para el tablero de recepcion. */
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
