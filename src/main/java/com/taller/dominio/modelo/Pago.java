package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public record Pago(BigDecimal monto, MetodoPago metodo, String referencia, Instant fecha) {

    public Pago {
        monto = Montos.normalizar(monto);
        if (!Montos.esPositivo(monto)) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
        }
        fecha = fecha == null ? Instant.now() : fecha;
    }

    public static Pago de(BigDecimal monto, MetodoPago metodo, String referencia) {
        return new Pago(monto, metodo, referencia, Instant.now());
    }
}
