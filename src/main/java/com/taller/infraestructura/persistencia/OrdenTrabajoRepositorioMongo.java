package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.OrdenTrabajoRepositorio;
import com.taller.infraestructura.persistencia.repositorio.OrdenTrabajoMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto OrdenTrabajoRepositorio. */
/**
 * Para qué sirve: Guarda y consulta órdenes de trabajo en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto OrdenTrabajoRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class OrdenTrabajoRepositorioMongo implements OrdenTrabajoRepositorio {

    private final OrdenTrabajoMongoRepository repo;

    public OrdenTrabajoRepositorioMongo(OrdenTrabajoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<OrdenTrabajo> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aOrden);
    }

    @Override
    public Optional<OrdenTrabajo> buscarPorNumero(String numero) {
        return repo.findByNumero(numero).map(Mapeadores::aOrden);
    }

    @Override
    public List<OrdenTrabajo> listarPorEstado(EstadoOrden estado) {
        return repo.findByEstado(estado.name()).stream().map(Mapeadores::aOrden).toList();
    }

    @Override
    public List<OrdenTrabajo> listarPorPlaca(String placa) {
        return repo.findByVehiculoPlaca(placa).stream().map(Mapeadores::aOrden).toList();
    }

    @Override
    public List<OrdenTrabajo> listarPorMecanico(String idMecanico) {
        return repo.findByMecanicoId(idMecanico).stream().map(Mapeadores::aOrden).toList();
    }

    @Override
    public long contar() {
        return repo.count();
    }

    @Override
    public OrdenTrabajo guardar(OrdenTrabajo orden) {
        return Mapeadores.aOrden(repo.save(Mapeadores.aDoc(orden)));
    }
}
