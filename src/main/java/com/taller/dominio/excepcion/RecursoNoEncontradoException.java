package com.taller.dominio.excepcion;

public class RecursoNoEncontradoException extends TallerException {

    public RecursoNoEncontradoException(String recurso, String identificador) {
        super("RECURSO_NO_ENCONTRADO", "No existe %s con identificador %s".formatted(recurso, identificador));
    }
}
