package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.OrdenTrabajoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "ordenes".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface OrdenTrabajoMongoRepository extends MongoRepository<OrdenTrabajoDoc, String> {

    java.util.Optional<OrdenTrabajoDoc> findByNumero(String numero);

    java.util.List<OrdenTrabajoDoc> findByEstado(String estado);

    java.util.List<OrdenTrabajoDoc> findByVehiculoPlaca(String placa);

    java.util.List<OrdenTrabajoDoc> findByMecanicoId(String idMecanico);
}
