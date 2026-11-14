package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.puerto.ClienteRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

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
