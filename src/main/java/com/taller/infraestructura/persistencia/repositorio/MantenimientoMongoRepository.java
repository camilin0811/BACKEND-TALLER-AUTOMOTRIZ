package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.MantenimientoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MantenimientoMongoRepository extends MongoRepository<MantenimientoDoc, String> {

    java.util.List<MantenimientoDoc> findByNotificadoFalseAndFechaSugeridaLessThanEqual(java.time.LocalDate hasta);
}
