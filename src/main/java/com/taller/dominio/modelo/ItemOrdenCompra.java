package com.taller.dominio.modelo;

import java.math.BigDecimal;

public class ItemOrdenCompra {

    private final String codigoRepuesto;
    private final int cantidad;
    private int cantidadRecibida;
    private final BigDecimal precioUnitario;

    public ItemOrdenCompra(String codigoRepuesto, int cantidad, int cantidadRecibida, BigDecimal precioUnitario) {
        this.codigoRepuesto = codigoRepuesto;
        this.cantidad = cantidad;
        this.cantidadRecibida = cantidadRecibida;
        this.precioUnitario = Montos.normalizar(precioUnitario);
    }

    public void recibir(int cantidadEntregada) {
        if (cantidadEntregada <= 0) {
            throw new IllegalArgumentException("La cantidad recibida debe ser mayor que cero");
        }
        this.cantidadRecibida = Math.min(cantidad, cantidadRecibida + cantidadEntregada);
    }

    public boolean estaCompleto() {
        return cantidadRecibida >= cantidad;
    }

    public int pendiente() {
        return Math.max(0, cantidad - cantidadRecibida);
    }

    public BigDecimal subtotal() {
        return Montos.multiplicar(precioUnitario, cantidad);
    }

    public String getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getCantidadRecibida() {
        return cantidadRecibida;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}
