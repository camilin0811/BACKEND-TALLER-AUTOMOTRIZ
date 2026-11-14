package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface FacturaRepositorio {

    java.util.Optional<Factura> buscarPorId(String id);

    java.util.Optional<Factura> buscarPorOrden(String idOrden);

    java.util.List<Factura> listarPorEstadoDian(EstadoDian estado);

    long contar();

    Factura guardar(Factura factura);
}
