package com.taller.dominio.excepcion;

/**
 * Para qué sirve: El recurso pedido no existe. ManejadorGlobalErrores la responde con 404.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class RecursoNoEncontradoException extends TallerException {

    public RecursoNoEncontradoException(String recurso, String identificador) {
        super("RECURSO_NO_ENCONTRADO", "No existe %s con identificador %s".formatted(recurso, identificador));
    }

    /**
     * Mensaje libre, para cuando nombrar el identificador buscado revelaria
     * algo que no debe salir (por ejemplo, en la consulta publica).
     */
    public static RecursoNoEncontradoException conMensaje(String mensaje) {
        return new RecursoNoEncontradoException(mensaje);
    }

    private RecursoNoEncontradoException(String mensaje) {
        super("RECURSO_NO_ENCONTRADO", mensaje);
    }
}
