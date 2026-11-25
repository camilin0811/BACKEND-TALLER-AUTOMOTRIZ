package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.FacturaRepositorio;
import com.taller.infraestructura.persistencia.repositorio.FacturaMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto FacturaRepositorio. */
/**
 * Para qué sirve: Guarda y consulta facturas en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto FacturaRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class FacturaRepositorioMongo implements FacturaRepositorio {

    private final FacturaMongoRepository repo;

    public FacturaRepositorioMongo(FacturaMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Factura> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aFactura);
    }

    @Override
    public Optional<Factura> buscarPorOrden(String idOrden) {
        return repo.findByIdOrden(idOrden).map(Mapeadores::aFactura);
    }

    @Override
    public List<Factura> listarPorEstadoDian(EstadoDian estado) {
        return repo.findByEstadoDian(estado.name()).stream().map(Mapeadores::aFactura).toList();
    }

    @Override
    public long contar() {
        return repo.count();
    }

    @Override
    public Factura guardar(Factura factura) {
        return Mapeadores.aFactura(repo.save(Mapeadores.aDoc(factura)));
    }
}
