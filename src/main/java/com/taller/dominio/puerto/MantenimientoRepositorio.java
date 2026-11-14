package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface MantenimientoRepositorio {

    java.util.List<MantenimientoPreventivo> listarPendientes(java.time.LocalDate hasta);

    MantenimientoPreventivo guardar(MantenimientoPreventivo mantenimiento);
}
