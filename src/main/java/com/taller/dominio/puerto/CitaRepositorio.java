package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface CitaRepositorio {

    java.util.Optional<Cita> buscarPorId(String id);

    java.util.List<Cita> listarPorFecha(java.time.LocalDate fecha);

    long contarEnFranja(java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    Cita guardar(Cita cita);
}
