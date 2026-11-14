package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.OrdenCompraDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdenCompraMongoRepository extends MongoRepository<OrdenCompraDoc, String> {

}
