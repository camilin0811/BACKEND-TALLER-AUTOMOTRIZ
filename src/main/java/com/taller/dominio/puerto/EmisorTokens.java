package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Usuario;

import java.util.Optional;

/** Emision y verificacion de los tokens de sesion. */
/**
 * Para qué sirve: Emite y valida tokens de sesión. Lo implementa ProveedorJwt.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface EmisorTokens {

    record Credencial(String token, long expiraEnSegundos) {
    }

    record Identidad(String idUsuario, String usuario, String rol) {
    }

    Credencial emitir(Usuario usuario);

    /** Vacio si el token es invalido, expirado o esta manipulado. */
    Optional<Identidad> verificar(String token);
}
