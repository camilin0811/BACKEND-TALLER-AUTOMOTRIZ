package com.taller.dominio.excepcion;

import com.taller.dominio.modelo.EstadoOrden;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Se lanza cuando se intenta una transicion que la maquina de estados no permite.
 *
 * <p>El mensaje enumera las transiciones validas a proposito: un error que solo
 * dice "operación no permitida" obliga a abrir el codigo para entenderlo.
 *
 * <p>Para qué sirve: Se intentó un cambio de estado que la máquina de estados no permite.
 * ManejadorGlobalErrores la responde con 409.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class TransicionInvalidaException extends TallerException {

    public TransicionInvalidaException(EstadoOrden desde, EstadoOrden hacia) {
        super("TRANSICION_INVALIDA", construirMensaje(desde, hacia));
    }

    private static String construirMensaje(EstadoOrden desde, EstadoOrden hacia) {
        Set<EstadoOrden> validas = desde.siguientesPosibles();
        if (validas.isEmpty()) {
            return "No se puede pasar de «%s» a «%s»: «%s» es un estado terminal."
                    .formatted(desde.etiqueta(), hacia.etiqueta(), desde.etiqueta());
        }
        String lista = validas.stream().map(EstadoOrden::etiqueta).sorted()
                .collect(Collectors.joining(", "));
        return "No se puede pasar de «%s» a «%s». Desde «%s» se puede pasar a: %s."
                .formatted(desde.etiqueta(), hacia.etiqueta(), desde.etiqueta(), lista);
    }
}
