package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Reglas de redondeo del dinero, en un solo lugar. */
public final class Montos {

    public static final int ESCALA = 2;
    public static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    private Montos() {
    }

    public static BigDecimal normalizar(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(ESCALA) : valor.setScale(ESCALA, REDONDEO);
    }

    public static BigDecimal cero() {
        return BigDecimal.ZERO.setScale(ESCALA);
    }

    public static BigDecimal multiplicar(BigDecimal valor, int cantidad) {
        return normalizar(normalizar(valor).multiply(BigDecimal.valueOf(cantidad)));
    }

    public static boolean esPositivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }
}
