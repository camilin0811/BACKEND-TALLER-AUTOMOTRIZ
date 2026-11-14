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

    RECIBIDA,
    EN_DIAGNOSTICO,
    COTIZADA,
    APROBADA,
    RECHAZADA,
    EN_REPARACION,
    EN_ESPERA_REPUESTO,
    CERRADA,
    FACTURADA,
    ENTREGADA;

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
