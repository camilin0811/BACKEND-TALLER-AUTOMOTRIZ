package com.taller.dominio.excepcion;

/**
 * Para qué sirve: Se intentó crear algo que ya existe (por ejemplo, un documento o una placa
 * repetidos). ManejadorGlobalErrores la responde con 409.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class RecursoDuplicadoException extends TallerException {

    public RecursoDuplicadoException(String mensaje) {
        super("RECURSO_DUPLICADO", mensaje);
    }
}
