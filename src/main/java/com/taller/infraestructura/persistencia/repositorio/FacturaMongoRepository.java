package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.FacturaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacturaMongoRepository extends MongoRepository<FacturaDoc, String> {

    java.util.Optional<FacturaDoc> findByIdOrden(String idOrden);

    java.util.List<FacturaDoc> findByEstadoDian(String estadoDian);
}
