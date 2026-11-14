package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.MovimientoInventarioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovimientoInventarioMongoRepository extends MongoRepository<MovimientoInventarioDoc, String> {

    java.util.List<MovimientoInventarioDoc> findByCodigoRepuestoOrderByFechaDesc(String codigoRepuesto);
}
