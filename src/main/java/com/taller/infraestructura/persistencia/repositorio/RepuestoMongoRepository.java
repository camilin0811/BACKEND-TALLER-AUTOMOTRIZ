package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.RepuestoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepuestoMongoRepository extends MongoRepository<RepuestoDoc, String> {

    java.util.Optional<RepuestoDoc> findByCodigo(String codigo);
}
