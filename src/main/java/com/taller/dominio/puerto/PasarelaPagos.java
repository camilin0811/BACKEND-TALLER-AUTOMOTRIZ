package com.taller.dominio.puerto;

import java.math.BigDecimal;

/**
 * Para qué sirve: Cobra pagos en línea. Lo implementa PasarelaPagoClient.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface PasarelaPagos {

    record Transaccion(boolean aprobada, String referencia, String motivoRechazo) {
    }

    Transaccion cobrar(BigDecimal monto, String descripcion);
}
