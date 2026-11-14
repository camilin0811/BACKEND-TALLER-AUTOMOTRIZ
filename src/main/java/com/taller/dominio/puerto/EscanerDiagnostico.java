package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Diagnostico;

/** Lectura del escaner OBD-II a traves del gateway IoT del taller. */
public interface EscanerDiagnostico {

    /** Lanza IntegracionNoDisponibleException si el escaner no responde. */
    Diagnostico escanear(String placa);
}
