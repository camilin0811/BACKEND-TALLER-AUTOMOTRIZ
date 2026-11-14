package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.MantenimientoRepositorio;
import com.taller.infraestructura.persistencia.repositorio.MantenimientoMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto MantenimientoRepositorio. */
@Repository
public class MantenimientoRepositorioMongo implements MantenimientoRepositorio {

    private final MantenimientoMongoRepository repo;

    public MantenimientoRepositorioMongo(MantenimientoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<MantenimientoPreventivo> listarPendientes(java.time.LocalDate hasta) {
        return repo.findByNotificadoFalseAndFechaSugeridaLessThanEqual(hasta)
                .stream().map(Mapeadores::aMantenimiento).toList();
    }

    @Override
    public MantenimientoPreventivo guardar(MantenimientoPreventivo mantenimiento) {
        return Mapeadores.aMantenimiento(repo.save(Mapeadores.aDoc(mantenimiento)));
    }
}
