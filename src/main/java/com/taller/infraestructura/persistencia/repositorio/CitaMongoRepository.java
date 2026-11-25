package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.CitaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "citas".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface CitaMongoRepository extends MongoRepository<CitaDoc, String> {

    java.util.List<CitaDoc> findByFechaHoraBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    long countByFechaHoraBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);
}
