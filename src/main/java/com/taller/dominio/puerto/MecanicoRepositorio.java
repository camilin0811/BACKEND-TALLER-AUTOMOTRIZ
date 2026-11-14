package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface MecanicoRepositorio {

    java.util.Optional<Mecanico> buscarPorId(String id);

    java.util.List<Mecanico> listarDisponibles();

    Mecanico guardar(Mecanico mecanico);
}
