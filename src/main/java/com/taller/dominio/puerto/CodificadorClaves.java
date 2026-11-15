package com.taller.dominio.puerto;

/**
 * Codificacion de claves.
 *
 * <p>Es un puerto para que el dominio y los casos de uso no dependan de la
 * libreria de turno. Cambiar BCrypt por Argon2 no toca ninguna regla.
 */
public interface CodificadorClaves {

    String codificar(String claveEnClaro);

    boolean coincide(String claveEnClaro, String hash);
}
