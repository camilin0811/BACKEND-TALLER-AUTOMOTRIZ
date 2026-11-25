package com.taller.dominio.excepcion;

/**
 * Para qué sirve: Una operación viola una regla del negocio. ManejadorGlobalErrores la responde con
 * 422.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class ReglaNegocioException extends TallerException {

    public ReglaNegocioException(String mensaje) {
        super("REGLA_NEGOCIO", mensaje);
    }

    public ReglaNegocioException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
