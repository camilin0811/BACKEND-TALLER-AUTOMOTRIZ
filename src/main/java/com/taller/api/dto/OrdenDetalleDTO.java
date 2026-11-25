package com.taller.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Para qué sirve: Vista completa de una orden para la pantalla de detalle, con su historial.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
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
                              String idFactura,
                              String idMecanico,
                              String motivoIngreso) {

    public record ItemDTO(String codigo, String descripcion, int cantidad, BigDecimal valor) {
    }

    /**
     * Un cambio de estado. {@code nombre} y {@code rol} los completa la API
     * interna; la publica no expone quien del taller hizo cada cambio.
     */
    public record HitoDTO(String estado, Instant fecha, String usuario, String nombre, String rol,
                          String tipo, String descripcion) {

        /** Cambio de estado, el caso de siempre. */
        public HitoDTO(String estado, Instant fecha, String usuario, String nombre, String rol) {
            this(estado, fecha, usuario, nombre, rol, "ESTADO", null);
        }
    }
}
