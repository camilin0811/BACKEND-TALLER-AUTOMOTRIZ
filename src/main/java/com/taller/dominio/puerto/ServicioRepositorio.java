package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface ServicioRepositorio {

    java.util.Optional<Servicio> buscarPorCodigo(String codigo);

    java.util.List<Servicio> listarActivos();

    Servicio guardar(Servicio servicio);
}
