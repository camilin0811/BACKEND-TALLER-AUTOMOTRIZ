package com.taller.dominio.modelo;

import java.math.BigDecimal;

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
