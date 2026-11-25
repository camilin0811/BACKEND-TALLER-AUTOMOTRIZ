package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 *
 * <p>Para qué sirve: Guarda y consulta órdenes de trabajo. Lo implementa OrdenTrabajoRepositorioMongo.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Repository: se comporta como una colección de objetos del dominio y esconde cómo se
 *     guardan.
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
