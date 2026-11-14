package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.ProveedorDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProveedorMongoRepository extends MongoRepository<ProveedorDoc, String> {

}
