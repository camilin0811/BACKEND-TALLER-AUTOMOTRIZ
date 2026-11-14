package com.taller.dominio.modelo;

import java.math.BigDecimal;

public record LineaServicio(String codigo, String descripcion, BigDecimal precio, double horasEstimadas) {

    public LineaServicio {
        precio = Montos.normalizar(precio);
    }
}
