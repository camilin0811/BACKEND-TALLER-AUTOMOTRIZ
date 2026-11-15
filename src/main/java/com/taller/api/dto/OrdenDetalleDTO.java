package com.taller.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrdenDetalleDTO(String id,
                              String numero,
                              String estado,
                              Instant fechaIngreso,
                              Instant fechaEntrega,
                              int kilometrajeIngreso,
                              String placa,
                              String descripcionVehiculo,
                              String cliente,
                              String mecanico,
                              List<String> codigosFalla,
                              String numeroCotizacion,
                              String estadoCotizacion,
                              List<ItemDTO> servicios,
                              List<ItemDTO> repuestos,
                              List<HitoDTO> historial,
                              BigDecimal total,
                              String idFactura) {

    public record ItemDTO(String codigo, String descripcion, int cantidad, BigDecimal valor) {
    }

    public record HitoDTO(String estado, Instant fecha, String usuario) {
    }
}
