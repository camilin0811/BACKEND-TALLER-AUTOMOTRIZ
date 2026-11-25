package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.UsuarioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "usuarios".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface UsuarioMongoRepository extends MongoRepository<UsuarioDoc, String> {

    Optional<UsuarioDoc> findByUsuario(String usuario);
}
