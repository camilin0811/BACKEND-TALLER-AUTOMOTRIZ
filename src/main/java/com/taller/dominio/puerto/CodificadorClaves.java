package com.taller.dominio.puerto;

/**
 * Codificacion de claves.
 *
 * <p>Es un puerto para que el dominio y los casos de uso no dependan de la
 * libreria de turno. Cambiar BCrypt por Argon2 no toca ninguna regla.
 *
 * <p>Para qué sirve: Codifica y verifica claves. Lo implementa CodificadorBCrypt.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface CodificadorClaves {

    String codificar(String claveEnClaro);

    boolean coincide(String claveEnClaro, String hash);
}
