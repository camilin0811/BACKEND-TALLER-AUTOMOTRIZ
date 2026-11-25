package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Factura;

/** Integracion con el proveedor tecnologico autorizado por la DIAN. */
/**
 * Para qué sirve: Envía la factura a la DIAN y devuelve su validación.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Strategy: tiene dos implementaciones intercambiables, FacturacionSimulada y
 *     DianFacturacionClient; una propiedad de configuración decide cuál se usa.
 */
public interface FacturacionElectronica {

    /** Resultado del envio: trae el CUFE si la DIAN valido, o el motivo del rechazo. */
    record Resultado(boolean validada, String cufe, String motivoRechazo) {

        public static Resultado validada(String cufe) {
            return new Resultado(true, cufe, null);
        }

        public static Resultado rechazada(String motivo) {
            return new Resultado(false, null, motivo);
        }
    }

    Resultado enviar(Factura factura);
}
