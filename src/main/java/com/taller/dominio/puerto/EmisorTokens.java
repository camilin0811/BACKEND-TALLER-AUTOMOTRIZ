package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Usuario;

import java.util.Optional;

/** Emision y verificacion de los tokens de sesion. */
public interface EmisorTokens {

    record Credencial(String token, long expiraEnSegundos) {
    }

    record Identidad(String idUsuario, String usuario, String rol) {
    }

    Credencial emitir(Usuario usuario);

    /** Vacio si el token es invalido, expirado o esta manipulado. */
    Optional<Identidad> verificar(String token);
}
