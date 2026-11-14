package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.ServicioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServicioMongoRepository extends MongoRepository<ServicioDoc, String> {

    java.util.Optional<ServicioDoc> findByCodigo(String codigo);

    java.util.List<ServicioDoc> findByActivoTrue();
}
