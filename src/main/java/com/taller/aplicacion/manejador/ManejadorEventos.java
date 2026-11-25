package com.taller.aplicacion.manejador;

import com.taller.dominio.evento.EventoDominio;

/**
 * Base de los cuatro motores de automatizacion.
 *
 * <p>Cada manejador declara que eventos soporta y como reacciona. Agregar una
 * consecuencia nueva al negocio es anadir una subclase, nunca modificar el
 * servicio que emite el evento (ADR-002).
 *
 * <p>Para qué sirve: Clase base de los cuatro motores de automatización (M1 a M4) que reaccionan a los
 * eventos del negocio.
 * <p>PATRÓN Observer (publicar/suscribir): define el contrato del observador (soporta y manejar).
 *     Agregar una reacción nueva es crear una subclase, sin tocar a quien emite el evento (principio
 *     abierto/cerrado).
 * <p>PATRÓN Herencia y polimorfismo: cada motor hereda de esta clase y aporta su propio comportamiento
 *     (polimorfismo).
 */
public abstract class ManejadorEventos {

    // PATRÓN Observer: cada suscriptor declara qué eventos le interesan...
    public abstract boolean soporta(String tipoEvento);

    // PATRÓN Observer: ...y cómo reacciona a ellos.
    public abstract void manejar(EventoDominio evento);

    /** Nombre legible para los registros. */
    public String nombre() {
        return getClass().getSimpleName();
    }
}
