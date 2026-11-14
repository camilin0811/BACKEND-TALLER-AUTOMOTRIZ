package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.ClienteDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteMongoRepository extends MongoRepository<ClienteDoc, String> {

    java.util.Optional<ClienteDoc> findByDocumento(String documento);
}
