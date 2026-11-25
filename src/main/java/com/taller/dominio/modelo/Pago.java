package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Para qué sirve: Un pago registrado a una factura: monto, método, referencia y fecha.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 * <p>PATRÓN Método fábrica estático: de() crea el objeto en un estado inicial válido y le da nombre a
 *     esa forma de crearlo.
 */
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
