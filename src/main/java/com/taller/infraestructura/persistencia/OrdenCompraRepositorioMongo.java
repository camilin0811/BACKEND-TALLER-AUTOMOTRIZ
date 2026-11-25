package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.OrdenCompraRepositorio;
import com.taller.infraestructura.persistencia.repositorio.OrdenCompraMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto OrdenCompraRepositorio. */
/**
 * Para qué sirve: Guarda y consulta órdenes de compra en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto OrdenCompraRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class OrdenCompraRepositorioMongo implements OrdenCompraRepositorio {

    private final OrdenCompraMongoRepository repo;

    public OrdenCompraRepositorioMongo(OrdenCompraMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<OrdenCompra> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aOrdenCompra);
    }

    @Override
    public List<OrdenCompra> listar() {
        return repo.findAll().stream().map(Mapeadores::aOrdenCompra).toList();
    }

    @Override
    public long contar() {
        return repo.count();
    }

    @Override
    public OrdenCompra guardar(OrdenCompra orden) {
        return Mapeadores.aOrdenCompra(repo.save(Mapeadores.aDoc(orden)));
    }
}
