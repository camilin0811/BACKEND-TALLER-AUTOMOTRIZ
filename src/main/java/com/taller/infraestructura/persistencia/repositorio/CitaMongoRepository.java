package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.CitaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CitaMongoRepository extends MongoRepository<CitaDoc, String> {

    java.util.List<CitaDoc> findByFechaHoraBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    long countByFechaHoraBetween(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);
}
