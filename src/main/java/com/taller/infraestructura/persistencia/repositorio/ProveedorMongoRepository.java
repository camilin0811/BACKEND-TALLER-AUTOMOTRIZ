package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.ProveedorDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "proveedores".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface ProveedorMongoRepository extends MongoRepository<ProveedorDoc, String> {

}
