package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.EmpleadoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "empleados".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface EmpleadoMongoRepository extends MongoRepository<EmpleadoDoc, String> {

    java.util.List<EmpleadoDoc> findByCargo(String cargo);

    java.util.List<EmpleadoDoc> findByCargoAndDisponibleTrue(String cargo);
}
