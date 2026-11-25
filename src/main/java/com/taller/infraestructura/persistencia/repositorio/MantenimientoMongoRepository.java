package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.MantenimientoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "mantenimientos".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface MantenimientoMongoRepository extends MongoRepository<MantenimientoDoc, String> {

    java.util.List<MantenimientoDoc> findByNotificadoFalseAndFechaSugeridaLessThanEqual(java.time.LocalDate hasta);
}
