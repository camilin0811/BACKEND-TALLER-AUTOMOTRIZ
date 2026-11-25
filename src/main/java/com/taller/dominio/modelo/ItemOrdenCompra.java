package com.taller.dominio.modelo;

import java.math.BigDecimal;

/**
 * Para qué sirve: Línea de una orden de compra: el repuesto y sus cantidades.
 * <p>PATRÓN Agregado y raíz de agregado (DDD): pertenece al agregado OrdenCompra.
 */
public class ItemOrdenCompra {

    private final String codigoRepuesto;
    private int cantidad;
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
        // Antes se topaba en silencio y el stock sumaba igual la cantidad
        // completa: recibir de mas inflaba el inventario.
        if (cantidadEntregada > pendiente()) {
            throw new com.taller.dominio.excepcion.ReglaNegocioException("RECEPCION_EXCEDE_PENDIENTE",
                    "Solo quedan %d unidades pendientes de %s".formatted(pendiente(), codigoRepuesto));
        }
        this.cantidadRecibida += cantidadEntregada;
    }

    void cambiarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.cantidad = nuevaCantidad;
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
