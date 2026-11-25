package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.ServicioRepositorio;
import com.taller.infraestructura.persistencia.repositorio.ServicioMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto ServicioRepositorio. */
/**
 * Para qué sirve: Guarda y consulta servicios del catálogo en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto ServicioRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class ServicioRepositorioMongo implements ServicioRepositorio {

    private final ServicioMongoRepository repo;

    public ServicioRepositorioMongo(ServicioMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        return repo.findByCodigo(codigo).map(Mapeadores::aServicio);
    }

    @Override
    public List<Servicio> listarActivos() {
        return repo.findByActivoTrue().stream().map(Mapeadores::aServicio).toList();
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        return Mapeadores.aServicio(repo.save(Mapeadores.aDoc(servicio)));
    }
}
