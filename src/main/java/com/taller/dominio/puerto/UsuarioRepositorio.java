package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio {

    Optional<Usuario> buscarPorUsuario(String usuario);

    Optional<Usuario> buscarPorId(String id);

    List<Usuario> listar();

    Usuario guardar(Usuario usuario);
}
