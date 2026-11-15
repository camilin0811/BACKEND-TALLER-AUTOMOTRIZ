package com.taller.dominio.excepcion;

/**
 * Usuario o clave incorrectos.
 *
 * <p>Tiene su propia clase para que la API responda 401 y no 422: "no se pudo
 * verificar quien es usted" no es lo mismo que "sus datos incumplen una regla".
 * El mensaje es siempre el mismo exista o no el usuario.
 */
public class CredencialesInvalidasException extends TallerException {

    public CredencialesInvalidasException() {
        super("CREDENCIALES_INVALIDAS", "Usuario o clave incorrectos");
    }
}
