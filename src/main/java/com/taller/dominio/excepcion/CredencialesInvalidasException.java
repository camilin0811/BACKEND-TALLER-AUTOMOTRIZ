package com.taller.dominio.excepcion;

/**
 * Usuario o clave incorrectos.
 *
 * <p>Tiene su propia clase para que la API responda 401 y no 422: "no se pudo
 * verificar quien es usted" no es lo mismo que "sus datos incumplen una regla".
 * El mensaje es siempre el mismo exista o no el usuario.
 *
 * <p>Para qué sirve: Usuario o clave incorrectos. ManejadorGlobalErrores la responde con 401.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class CredencialesInvalidasException extends TallerException {

    public CredencialesInvalidasException() {
        super("CREDENCIALES_INVALIDAS", "Usuario o clave incorrectos");
    }
}
