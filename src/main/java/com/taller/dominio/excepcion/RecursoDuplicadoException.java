package com.taller.dominio.excepcion;

public class RecursoDuplicadoException extends TallerException {

    public RecursoDuplicadoException(String mensaje) {
        super("RECURSO_DUPLICADO", mensaje);
    }
}
