package com.taller.dominio.excepcion;

import com.taller.dominio.modelo.EstadoOrden;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Se lanza cuando se intenta una transicion que la maquina de estados no permite.
 *
 * <p>El mensaje enumera las transiciones validas a proposito: un error que solo
 * dice "operacion no permitida" obliga a abrir el codigo para entenderlo.
 */
public class TransicionInvalidaException extends TallerException {

    public TransicionInvalidaException(EstadoOrden desde, EstadoOrden hacia) {
        super("TRANSICION_INVALIDA", construirMensaje(desde, hacia));
    }

    private static String construirMensaje(EstadoOrden desde, EstadoOrden hacia) {
        Set<EstadoOrden> validas = desde.siguientesPosibles();
        if (validas.isEmpty()) {
            return "No se puede pasar de %s a %s. %s es un estado terminal."
                    .formatted(desde, hacia, desde);
        }
        String lista = validas.stream().map(Enum::name).sorted().collect(Collectors.joining(", "));
        return "No se puede pasar de %s a %s. Transiciones validas desde %s: %s"
                .formatted(desde, hacia, desde, lista);
    }
}
