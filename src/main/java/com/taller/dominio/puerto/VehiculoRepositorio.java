package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface VehiculoRepositorio {

    java.util.Optional<Vehiculo> buscarPorId(String id);

    java.util.Optional<Vehiculo> buscarPorPlaca(String placa);

    java.util.List<Vehiculo> listarPorCliente(String idCliente);

    boolean existePlaca(String placa);

    Vehiculo guardar(Vehiculo vehiculo);
}
