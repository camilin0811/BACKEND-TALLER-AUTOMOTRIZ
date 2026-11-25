package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.VehiculoRepositorio;
import com.taller.infraestructura.persistencia.repositorio.VehiculoMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto VehiculoRepositorio. */
/**
 * Para qué sirve: Guarda y consulta vehículos en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto VehiculoRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class VehiculoRepositorioMongo implements VehiculoRepositorio {

    private final VehiculoMongoRepository repo;

    public VehiculoRepositorioMongo(VehiculoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Vehiculo> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aVehiculo);
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return repo.findByPlaca(normalizar(placa)).map(Mapeadores::aVehiculo);
    }

    @Override
    public List<Vehiculo> listarPorCliente(String idCliente) {
        return repo.findByIdCliente(idCliente).stream().map(Mapeadores::aVehiculo).toList();
    }

    @Override
    public boolean existePlaca(String placa) {
        return repo.existsByPlaca(normalizar(placa));
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        return Mapeadores.aVehiculo(repo.save(Mapeadores.aDoc(vehiculo)));
    }

    private static String normalizar(String placa) {
        return placa == null ? null : placa.trim().toUpperCase().replace("-", "");
    }
}
