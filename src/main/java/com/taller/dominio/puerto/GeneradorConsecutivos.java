package com.taller.dominio.puerto;

/** Numeros de negocio legibles: OT-0100, COT-0100, FV-0100, OC-0012. */
public interface GeneradorConsecutivos {

    String siguienteOrdenTrabajo();

    String siguienteCotizacion();

    String siguienteFactura();

    String siguienteOrdenCompra();

    String siguienteCita();
}
