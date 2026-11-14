package com.taller.dominio.puerto;

import java.math.BigDecimal;

public interface PasarelaPagos {

    record Transaccion(boolean aprobada, String referencia, String motivoRechazo) {
    }

    Transaccion cobrar(BigDecimal monto, String descripcion);
}
