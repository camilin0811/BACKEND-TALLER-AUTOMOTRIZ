package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Factura;

/** Integracion con el proveedor tecnologico autorizado por la DIAN. */
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
