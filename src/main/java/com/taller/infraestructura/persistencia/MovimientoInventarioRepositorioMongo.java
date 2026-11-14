package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.MovimientoInventarioRepositorio;
import com.taller.infraestructura.persistencia.repositorio.MovimientoInventarioMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto MovimientoInventarioRepositorio. */
@Repository
public class MovimientoInventarioRepositorioMongo implements MovimientoInventarioRepositorio {

    private final MovimientoInventarioMongoRepository repo;

    public MovimientoInventarioRepositorioMongo(MovimientoInventarioMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<MovimientoInventario> listarPorRepuesto(String codigoRepuesto) {
        return repo.findByCodigoRepuestoOrderByFechaDesc(codigoRepuesto)
                .stream().map(Mapeadores::aMovimiento).toList();
    }

    @Override
    public MovimientoInventario guardar(MovimientoInventario movimiento) {
        return Mapeadores.aMovimiento(repo.save(Mapeadores.aDoc(movimiento)));
    }
}
