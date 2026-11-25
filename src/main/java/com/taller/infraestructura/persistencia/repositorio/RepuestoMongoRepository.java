package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.RepuestoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "repuestos".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface RepuestoMongoRepository extends MongoRepository<RepuestoDoc, String> {

    java.util.Optional<RepuestoDoc> findByCodigo(String codigo);
}
