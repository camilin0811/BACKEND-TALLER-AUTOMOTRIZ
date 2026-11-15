package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.UsuarioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioMongoRepository extends MongoRepository<UsuarioDoc, String> {

    Optional<UsuarioDoc> findByUsuario(String usuario);
}
