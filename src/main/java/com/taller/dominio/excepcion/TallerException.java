package com.taller.dominio.excepcion;

/**
 * Raiz de las excepciones de negocio. Todas llevan un codigo estable que la API
 * expone al cliente y un mensaje legible que puede cambiar sin romper contratos.
 */
public abstract class TallerException extends RuntimeException {

    private final String codigo;

    protected TallerException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
