package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface OrdenCompraRepositorio {

    java.util.Optional<OrdenCompra> buscarPorId(String id);

    java.util.List<OrdenCompra> listar();

    long contar();

    OrdenCompra guardar(OrdenCompra orden);
}
