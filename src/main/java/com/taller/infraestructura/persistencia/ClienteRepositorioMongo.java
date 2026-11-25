package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.ClienteRepositorio;
import com.taller.infraestructura.persistencia.repositorio.ClienteMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adaptador de MongoDB para el puerto ClienteRepositorio. */
/**
 * Para qué sirve: Guarda y consulta clientes en MongoDB.
 * <p>PATRÓN Adapter: implementa el puerto ClienteRepositorio del dominio usando Spring Data y
 *     Mapeadores.
 * <p>PATRÓN Repository: es la implementación concreta del repositorio.
 */
@Repository
public class ClienteRepositorioMongo implements ClienteRepositorio {

    private final ClienteMongoRepository repo;

    public ClienteRepositorioMongo(ClienteMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return repo.findById(id).map(Mapeadores::aCliente);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return repo.findByDocumento(documento).map(Mapeadores::aCliente);
    }

    @Override
    public List<Cliente> listar() {
        return repo.findAll().stream().map(Mapeadores::aCliente).toList();
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return Mapeadores.aCliente(repo.save(Mapeadores.aDoc(cliente)));
    }
}
