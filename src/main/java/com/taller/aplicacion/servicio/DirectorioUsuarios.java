package com.taller.aplicacion.servicio;

import com.taller.dominio.modelo.Usuario;
import com.taller.dominio.puerto.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Traduce el usuario guardado en cada cambio de estado a quien lo hizo.
 *
 * <p>El historial guarda el nombre de usuario, que es estable; el nombre de
 * la persona se resuelve al leer, asi un cambio de nombre no deja el
 * historial desactualizado. Lo que no corresponde a un usuario (datos de
 * demostracion o procesos automaticos) se muestra como "Sistema".
 */
@Service
public class DirectorioUsuarios {

    public record Autor(String nombre, String rol) {
        static final Autor SISTEMA = new Autor("Sistema", null);
    }

    private final UsuarioRepositorio usuarios;

    public DirectorioUsuarios(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    /** Una sola consulta para todo un historial, no una por cada cambio. */
    public Function<String, Autor> resolutor() {
        Map<String, Usuario> porUsuario = usuarios.listar().stream()
                .collect(Collectors.toMap(Usuario::getUsuario, u -> u, (a, b) -> a));
        return usuario -> {
            Usuario u = usuario == null ? null : porUsuario.get(usuario.trim().toLowerCase());
            return u == null ? Autor.SISTEMA : new Autor(u.getNombre(), u.getRol().name());
        };
    }
}
