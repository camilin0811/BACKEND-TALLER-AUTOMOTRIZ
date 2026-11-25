package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.VehiculoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "vehiculos".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface VehiculoMongoRepository extends MongoRepository<VehiculoDoc, String> {

    java.util.Optional<VehiculoDoc> findByPlaca(String placa);

    java.util.List<VehiculoDoc> findByIdCliente(String idCliente);

    boolean existsByPlaca(String placa);
}
