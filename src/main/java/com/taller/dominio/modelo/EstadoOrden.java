package com.taller.dominio.modelo;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Maquina de estados de la orden de trabajo.
 *
 * <p>Es la unica maquina de estados del sistema y la fuente de todos los eventos
 * de dominio. Las transiciones se declaran aqui, no en los servicios: asi es
 * imposible llegar a un estado invalido por mucho que cambie el codigo de arriba.
 *
 * <p>Ver ADR-009 en la wiki.
 */
public enum EstadoOrden {

    RECIBIDA("Recibida"),
    EN_DIAGNOSTICO("En diagnóstico"),
    COTIZADA("Cotizada"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    EN_REPARACION("En reparación"),
    EN_ESPERA_REPUESTO("En espera de repuesto"),
    CERRADA("Cerrada"),
    FACTURADA("Facturada"),
    ENTREGADA("Entregada");

    private final String etiqueta;

    EstadoOrden(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Nombre para personas. Los mensajes de error lo usan en lugar del nombre
     * del enum, que es un identificador tecnico y llega tal cual a la pantalla.
     */
    public String etiqueta() {
        return etiqueta;
    }

    private static final Map<EstadoOrden, Set<EstadoOrden>> TRANSICIONES = new EnumMap<>(EstadoOrden.class);

    static {
        TRANSICIONES.put(RECIBIDA, EnumSet.of(EN_DIAGNOSTICO));
        TRANSICIONES.put(EN_DIAGNOSTICO, EnumSet.of(COTIZADA));
        TRANSICIONES.put(COTIZADA, EnumSet.of(APROBADA, RECHAZADA));
        TRANSICIONES.put(APROBADA, EnumSet.of(EN_REPARACION));
        TRANSICIONES.put(RECHAZADA, EnumSet.of(ENTREGADA));
        TRANSICIONES.put(EN_REPARACION, EnumSet.of(EN_ESPERA_REPUESTO, CERRADA));
        TRANSICIONES.put(EN_ESPERA_REPUESTO, EnumSet.of(EN_REPARACION));
        TRANSICIONES.put(CERRADA, EnumSet.of(FACTURADA));
        TRANSICIONES.put(FACTURADA, EnumSet.of(ENTREGADA));
        TRANSICIONES.put(ENTREGADA, EnumSet.noneOf(EstadoOrden.class));
    }

    /** Estados a los que se puede pasar desde este. Vacio si es terminal. */
    public Set<EstadoOrden> siguientesPosibles() {
        return Collections.unmodifiableSet(TRANSICIONES.get(this));
    }

    public boolean permiteTransicionA(EstadoOrden destino) {
        return TRANSICIONES.get(this).contains(destino);
    }

    public boolean esTerminal() {
        return TRANSICIONES.get(this).isEmpty();
    }

    /** Una orden entregada queda en solo lectura. */
    public boolean admiteModificacion() {
        return this != ENTREGADA;
    }

    /** Solo se consumen repuestos mientras hay trabajo en curso. */
    public boolean permiteConsumoDeRepuestos() {
        return this == EN_REPARACION;
    }
}
