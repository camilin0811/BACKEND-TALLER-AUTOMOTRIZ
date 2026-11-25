package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 *
 * <p>Para qué sirve: Guarda y consulta mantenimientos preventivos. Lo implementa
 * MantenimientoRepositorioMongo.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Repository: se comporta como una colección de objetos del dominio y esconde cómo se
 *     guardan.
 */
public interface MantenimientoRepositorio {

    java.util.List<MantenimientoPreventivo> listarPendientes(java.time.LocalDate hasta);

    MantenimientoPreventivo guardar(MantenimientoPreventivo mantenimiento);
}
