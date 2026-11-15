package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.Rol;
import com.taller.dominio.modelo.Usuario;
import com.taller.dominio.puerto.UsuarioRepositorio;
import com.taller.infraestructura.persistencia.documento.UsuarioDoc;
import com.taller.infraestructura.persistencia.repositorio.UsuarioMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositorioMongo implements UsuarioRepositorio {

    private final UsuarioMongoRepository repo;

    public UsuarioRepositorioMongo(UsuarioMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return repo.findByUsuario(usuario).map(UsuarioRepositorioMongo::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return repo.findById(id).map(UsuarioRepositorioMongo::aDominio);
    }

    @Override
    public List<Usuario> listar() {
        return repo.findAll().stream().map(UsuarioRepositorioMongo::aDominio).toList();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioDoc d = new UsuarioDoc();
        d.id = usuario.getId();
        d.usuario = usuario.getUsuario();
        d.hashClave = usuario.getHashClave();
        d.rol = usuario.getRol().name();
        d.nombre = usuario.getNombre();
        d.idPersona = usuario.getIdPersona();
        d.activo = usuario.isActivo();
        d.ultimoAcceso = usuario.getUltimoAcceso();
        return aDominio(repo.save(d));
    }

    private static Usuario aDominio(UsuarioDoc d) {
        return new Usuario(d.id, d.usuario, d.hashClave, Rol.valueOf(d.rol), d.nombre,
                d.idPersona, d.activo, d.ultimoAcceso);
    }
}
