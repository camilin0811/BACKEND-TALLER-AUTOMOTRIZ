package com.taller.dominio.puerto;

import com.taller.dominio.modelo.Diagnostico;

/** Lectura del escaner OBD-II a traves del gateway IoT del taller. */
/**
 * Para qué sirve: Lee el escáner OBD-II del taller. Lo implementa ObdScannerAdapter.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface EscanerDiagnostico {

    /** Lanza IntegracionNoDisponibleException si el escaner no responde. */
    Diagnostico escanear(String placa);
}
