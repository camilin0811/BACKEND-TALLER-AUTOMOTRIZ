package com.taller.dominio.modelo;

import java.time.Instant;

/**
 * Un hecho del historial de la orden que no es un cambio de estado, como
 * asignar o cambiar el mecanico. Queda con quien lo hizo y cuando, igual
 * que un cambio de estado, para que el historial cuente la orden completa.
 */
public record AnotacionHistorial(Instant fecha, String usuario, String tipo, String descripcion) {

    public static final String MECANICO = "MECANICO";
}
