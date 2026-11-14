package com.taller.dominio.modelo;

import java.time.Instant;

/** Trazabilidad de cada entrada y salida. El stock es la suma de estos. */
public class MovimientoInventario {

    private String id;
    private final String codigoRepuesto;
    private final TipoMovimiento tipo;
    private final int cantidad;
    private final String motivo;
    private final String idOrden;
    private final int stockResultante;
    private final Instant fecha;

    public MovimientoInventario(String id, String codigoRepuesto, TipoMovimiento tipo, int cantidad,
                                String motivo, String idOrden, int stockResultante, Instant fecha) {
        this.id = id;
        this.codigoRepuesto = codigoRepuesto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.idOrden = idOrden;
        this.stockResultante = stockResultante;
        this.fecha = fecha == null ? Instant.now() : fecha;
    }

    public static MovimientoInventario salida(String codigoRepuesto, int cantidad, String idOrden,
                                              int stockResultante) {
        return new MovimientoInventario(null, codigoRepuesto, TipoMovimiento.SALIDA, cantidad,
                "Consumo en orden %s".formatted(idOrden), idOrden, stockResultante, Instant.now());
    }

    public static MovimientoInventario entrada(String codigoRepuesto, int cantidad, String motivo,
                                               int stockResultante) {
        return new MovimientoInventario(null, codigoRepuesto, TipoMovimiento.ENTRADA, cantidad,
                motivo, null, stockResultante, Instant.now());
    }

    /** Signo del movimiento, para auditar que la suma cuadre con el stock. */
    public int cantidadConSigno() {
        return tipo == TipoMovimiento.SALIDA ? -cantidad : cantidad;
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public Instant getFecha() {
        return fecha;
    }
}
