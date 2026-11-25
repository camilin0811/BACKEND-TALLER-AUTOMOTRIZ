package com.taller.dominio.puerto;

/** Numeros de negocio legibles: OT-0100, COT-0100, FV-0100, OC-0012. */
/**
 * Para qué sirve: Entrega números consecutivos de negocio (órdenes, facturas...). Lo implementa
 * GeneradorConsecutivosMongo.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface GeneradorConsecutivos {

    String siguienteOrdenTrabajo();

    String siguienteCotizacion();

    String siguienteFactura();

    String siguienteOrdenCompra();

    String siguienteCita();
}
