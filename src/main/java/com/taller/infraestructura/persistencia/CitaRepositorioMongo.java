package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.CitaRepositorio;
import com.taller.infraestructura.persistencia.repositorio.CitaMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto CitaRepositorio. */
/**
 * Para qué sirve: Guarda y consulta citas en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto CitaRepositorio del dominio usando Spring Data y Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class CitaRepositorioMongo implements CitaRepositorio {

    private final CitaMongoRepository repo;

    public CitaRepositorioMongo(CitaMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Cita> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aCita);
    }

    @Override
    public List<Cita> listarPorFecha(java.time.LocalDate fecha) {
        return repo.findByFechaHoraBetween(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream().map(Mapeadores::aCita).toList();
    }

    @Override
    public long contarEnFranja(java.time.LocalDateTime desde, java.time.LocalDateTime hasta) {
        return repo.countByFechaHoraBetween(desde, hasta);
    }

    @Override
    public Cita guardar(Cita cita) {
        return Mapeadores.aCita(repo.save(Mapeadores.aDoc(cita)));
    }
}
