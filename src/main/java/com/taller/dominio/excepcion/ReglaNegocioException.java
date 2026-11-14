package com.taller.dominio.excepcion;

public class ReglaNegocioException extends TallerException {

    public ReglaNegocioException(String mensaje) {
        super("REGLA_NEGOCIO", mensaje);
    }

    public ReglaNegocioException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
