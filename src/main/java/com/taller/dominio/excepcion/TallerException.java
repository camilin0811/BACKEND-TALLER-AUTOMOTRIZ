package com.taller.dominio.excepcion;

/**
 * Raiz de las excepciones de negocio. Todas llevan un codigo estable que la API
 * expone al cliente y un mensaje legible que puede cambiar sin romper contratos.
 *
 * <p>Para qué sirve: Raíz de las excepciones de negocio: todas llevan un código estable que la API
 * expone al cliente.
 * <p>PATRÓN Herencia y polimorfismo: jerarquía de excepciones: cada subclase representa un tipo de
 *     falla y ManejadorGlobalErrores elige el código HTTP según la clase.
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
