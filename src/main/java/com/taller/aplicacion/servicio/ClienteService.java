package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.puerto.ClienteRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Para qué sirve: Registra clientes y los busca por id o por documento.
 * <p>PATRÓN Service Layer (capa de servicio, fachada de casos de uso): orquesta el caso de uso: carga
 *     del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta
 *     fachada.
 * <p>PATRÓN Inyección de dependencias / Inversión de control: recibe los puertos (interfaces del
 *     dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
 */
@Service
public class ClienteService {

    private final ClienteRepositorio clientes;

    public ClienteService(ClienteRepositorio clientes) {
        this.clientes = clientes;
    }

    public Cliente registrar(String nombre, String documento, String telefono, String email, String direccion) {
        clientes.buscarPorDocumento(documento).ifPresent(existente -> {
            throw new RecursoDuplicadoException("Ya existe un cliente con documento %s".formatted(documento));
        });
        return clientes.guardar(Cliente.nuevo(nombre, documento, telefono, email, direccion));
    }

    public Cliente obtener(String id) {
        return clientes.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("cliente", id));
    }

    public Cliente obtenerPorDocumento(String documento) {
        return clientes.buscarPorDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException("cliente con documento", documento));
    }

    public List<Cliente> listar() {
        return clientes.listar();
    }
}
