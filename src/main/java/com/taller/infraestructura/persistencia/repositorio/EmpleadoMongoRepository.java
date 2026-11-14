package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.EmpleadoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmpleadoMongoRepository extends MongoRepository<EmpleadoDoc, String> {

    java.util.List<EmpleadoDoc> findByCargo(String cargo);

    java.util.List<EmpleadoDoc> findByCargoAndDisponibleTrue(String cargo);
}
