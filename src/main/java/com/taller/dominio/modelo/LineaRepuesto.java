package com.taller.dominio.modelo;

import java.math.BigDecimal;

/**
 * Para qué sirve: Repuesto cargado a una orden, con su cantidad y el precio del momento.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 */
public record LineaRepuesto(String codigo, String nombre, int cantidad, BigDecimal precioUnitario) {

    public LineaRepuesto {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        precioUnitario = Montos.normalizar(precioUnitario);
    }

    public BigDecimal subtotal() {
        return Montos.multiplicar(precioUnitario, cantidad);
    }
}
