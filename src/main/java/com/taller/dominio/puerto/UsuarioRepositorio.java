package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Para qué sirve: Guarda y consulta usuarios. Lo implementa UsuarioRepositorioMongo.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Repository: se comporta como una colección de objetos del dominio y esconde cómo se
 *     guardan.
 */
public interface UsuarioRepositorio {

    Optional<Usuario> buscarPorUsuario(String usuario);

    Optional<Usuario> buscarPorId(String id);

    List<Usuario> listar();

    Usuario guardar(Usuario usuario);
}
