package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.ServicioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "servicios".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface ServicioMongoRepository extends MongoRepository<ServicioDoc, String> {

    java.util.Optional<ServicioDoc> findByCodigo(String codigo);

    java.util.List<ServicioDoc> findByActivoTrue();
}
