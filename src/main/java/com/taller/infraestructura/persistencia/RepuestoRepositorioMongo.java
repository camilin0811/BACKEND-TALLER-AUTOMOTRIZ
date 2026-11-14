package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.RepuestoRepositorio;
import com.taller.infraestructura.persistencia.repositorio.RepuestoMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto RepuestoRepositorio. */
@Repository
public class RepuestoRepositorioMongo implements RepuestoRepositorio {

    private final RepuestoMongoRepository repo;

    public RepuestoRepositorioMongo(RepuestoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Repuesto> buscarPorCodigo(String codigo) {
        return repo.findByCodigo(codigo).map(Mapeadores::aRepuesto);
    }

    @Override
    public List<Repuesto> listar() {
        return repo.findAll().stream().map(Mapeadores::aRepuesto).toList();
    }

    @Override
    public List<Repuesto> listarBajoMinimo() {
        return repo.findAll().stream().map(Mapeadores::aRepuesto)
                .filter(Repuesto::requiereReposicion).toList();
    }

    @Override
    public Repuesto guardar(Repuesto repuesto) {
        return Mapeadores.aRepuesto(repo.save(Mapeadores.aDoc(repuesto)));
    }
}
