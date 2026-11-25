package com.taller.dominio.modelo;

import java.math.BigDecimal;

/**
 * Para qué sirve: Servicio de mano de obra agregado a la orden, con precio y horas estimadas.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 */
public record LineaServicio(String codigo, String descripcion, BigDecimal precio, double horasEstimadas) {

    public LineaServicio {
        precio = Montos.normalizar(precio);
    }
}
