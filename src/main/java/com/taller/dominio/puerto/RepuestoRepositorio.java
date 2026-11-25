package com.taller.dominio.puerto;

import com.taller.dominio.modelo.*;

/**
 * Puerto de persistencia. El dominio declara lo que necesita; la
 * infraestructura decide como cumplirlo.
 *
 * <p>Para qué sirve: Guarda y consulta repuestos. Lo implementa RepuestoRepositorioMongo.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Repository: se comporta como una colección de objetos del dominio y esconde cómo se
 *     guardan.
 */
public interface RepuestoRepositorio {

    java.util.Optional<Repuesto> buscarPorCodigo(String codigo);

    java.util.List<Repuesto> listar();

    java.util.List<Repuesto> listarBajoMinimo();

    Repuesto guardar(Repuesto repuesto);
}
