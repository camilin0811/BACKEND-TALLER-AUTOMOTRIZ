package com.taller.dominio.modelo;

import java.math.BigDecimal;

/** Item del catalogo de mano de obra. */
/**
 * Para qué sirve: Servicio del catálogo de mano de obra: código, descripción, precio y horas
 * estimadas.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 */
public class Servicio {

    private String id;
    private final String codigo;
    private String descripcion;
    private BigDecimal precio;
    private double horasEstimadas;
    private boolean activo;

    public Servicio(String id, String codigo, String descripcion, BigDecimal precio,
                    double horasEstimadas, boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = Montos.normalizar(precio);
        this.horasEstimadas = horasEstimadas;
        this.activo = activo;
    }

    public LineaServicio aLinea() {
        return new LineaServicio(codigo, descripcion, precio, horasEstimadas);
    }

    public void cambiarPrecio(BigDecimal nuevoPrecio) {
        if (!Montos.esPositivo(nuevoPrecio)) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.precio = Montos.normalizar(nuevoPrecio);
    }

    public void desactivar() {
        this.activo = false;
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public boolean isActivo() {
        return activo;
    }
}
