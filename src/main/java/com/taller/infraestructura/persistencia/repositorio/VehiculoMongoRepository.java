package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.VehiculoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehiculoMongoRepository extends MongoRepository<VehiculoDoc, String> {

    java.util.Optional<VehiculoDoc> findByPlaca(String placa);

    java.util.List<VehiculoDoc> findByIdCliente(String idCliente);

    boolean existsByPlaca(String placa);
}
