package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.MecanicoRepositorio;
import com.taller.infraestructura.persistencia.repositorio.EmpleadoMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto MecanicoRepositorio. */
@Repository
public class MecanicoRepositorioMongo implements MecanicoRepositorio {

    private final EmpleadoMongoRepository repo;

    public MecanicoRepositorioMongo(EmpleadoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Mecanico> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aMecanico);
    }

    @Override
    public List<Mecanico> listarDisponibles() {
        return repo.findByCargoAndDisponibleTrue("MECANICO").stream().map(Mapeadores::aMecanico).toList();
    }

    @Override
    public Mecanico guardar(Mecanico mecanico) {
        return Mapeadores.aMecanico(repo.save(Mapeadores.aDoc(mecanico)));
    }
}
