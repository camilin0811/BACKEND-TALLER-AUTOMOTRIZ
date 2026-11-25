package com.taller.dominio.excepcion;

/**
 * Para qué sirve: Un servicio externo (escáner, DIAN, WhatsApp o pasarela de pagos) no respondió.
 * ManejadorGlobalErrores la responde con 503.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
public class IntegracionNoDisponibleException extends TallerException {

    public IntegracionNoDisponibleException(String servicio, String detalle) {
        super("INTEGRACION_NO_DISPONIBLE",
                "El servicio %s no esta disponible: %s".formatted(servicio, detalle));
    }
}
