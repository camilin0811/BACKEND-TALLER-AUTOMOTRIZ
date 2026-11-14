package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface RepuestoRepositorio {

    java.util.Optional<Repuesto> buscarPorCodigo(String codigo);

    java.util.List<Repuesto> listar();

    java.util.List<Repuesto> listarBajoMinimo();

    Repuesto guardar(Repuesto repuesto);
}
