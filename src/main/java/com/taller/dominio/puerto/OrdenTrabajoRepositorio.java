package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 */
public interface OrdenTrabajoRepositorio {

    java.util.Optional<OrdenTrabajo> buscarPorId(String id);

    java.util.Optional<OrdenTrabajo> buscarPorNumero(String numero);

    java.util.List<OrdenTrabajo> listarPorEstado(EstadoOrden estado);

    java.util.List<OrdenTrabajo> listarPorPlaca(String placa);

    java.util.List<OrdenTrabajo> listarPorMecanico(String idMecanico);

    long contar();

    OrdenTrabajo guardar(OrdenTrabajo orden);
}
