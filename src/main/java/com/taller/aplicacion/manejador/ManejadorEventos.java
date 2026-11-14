package com.taller.aplicacion.manejador;

import com.taller.dominio.evento.EventoDominio;

/**
 * Base de los cuatro motores de automatizacion.
 *
 * <p>Cada manejador declara que eventos soporta y como reacciona. Agregar una
 * consecuencia nueva al negocio es anadir una subclase, nunca modificar el
 * servicio que emite el evento (ADR-002).
 */
public abstract class ManejadorEventos {

    public abstract boolean soporta(String tipoEvento);

    public abstract void manejar(EventoDominio evento);

    /** Nombre legible para los registros. */
    public String nombre() {
        return getClass().getSimpleName();
    }
}
