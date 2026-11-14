package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface ClienteRepositorio {

    java.util.Optional<Cliente> buscarPorId(String id);

    java.util.Optional<Cliente> buscarPorDocumento(String documento);

    java.util.List<Cliente> listar();

    Cliente guardar(Cliente cliente);
}
