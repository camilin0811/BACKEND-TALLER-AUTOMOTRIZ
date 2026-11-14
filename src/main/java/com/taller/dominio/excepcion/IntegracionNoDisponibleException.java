package com.taller.dominio.excepcion;

public class IntegracionNoDisponibleException extends TallerException {

    public IntegracionNoDisponibleException(String servicio, String detalle) {
        super("INTEGRACION_NO_DISPONIBLE",
                "El servicio %s no esta disponible: %s".formatted(servicio, detalle));
    }
}
