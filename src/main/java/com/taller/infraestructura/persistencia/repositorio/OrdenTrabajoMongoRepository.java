package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.OrdenTrabajoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdenTrabajoMongoRepository extends MongoRepository<OrdenTrabajoDoc, String> {

    java.util.Optional<OrdenTrabajoDoc> findByNumero(String numero);

    java.util.List<OrdenTrabajoDoc> findByEstado(String estado);

    java.util.List<OrdenTrabajoDoc> findByVehiculoPlaca(String placa);

    java.util.List<OrdenTrabajoDoc> findByMecanicoId(String idMecanico);
}
