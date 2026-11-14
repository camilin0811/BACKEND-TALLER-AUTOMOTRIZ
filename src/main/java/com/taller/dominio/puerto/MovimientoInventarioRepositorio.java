package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface MovimientoInventarioRepositorio {

    java.util.List<MovimientoInventario> listarPorRepuesto(String codigoRepuesto);

    MovimientoInventario guardar(MovimientoInventario movimiento);
}
