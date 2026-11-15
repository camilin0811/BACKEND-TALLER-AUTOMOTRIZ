package com.taller.infraestructura.integracion;

import com.taller.dominio.modelo.Factura;
import com.taller.dominio.modelo.ReferenciaCliente;
import com.taller.dominio.puerto.FacturacionElectronica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Facturacion simulada")
class FacturacionSimuladaTest {

    private final FacturacionSimulada simulador = new FacturacionSimulada();

    private Factura facturaDe(String numero) {
        Factura f = Factura.emitirPara("ot-1",
                new ReferenciaCliente("cli-1", "Cliente de Prueba", "+573000000001"),
                new BigDecimal("300000"), new BigDecimal("0.19"));
        f.asignarNumero(numero);
        return f;
    }

    @Test
    @DisplayName("devuelve la factura como validada con un CUFE")
    void validaConCufe() {
        FacturacionElectronica.Resultado r = simulador.enviar(facturaDe("FV-0001"));

        assertThat(r.validada()).isTrue();
        assertThat(r.cufe()).isNotBlank();
        assertThat(r.motivoRechazo()).isNull();
    }

    @Test
    @DisplayName("el CUFE lleva el prefijo SIM para que nadie lo confunda con uno real")
    void prefijoVisible() {
        assertThat(simulador.enviar(facturaDe("FV-0001")).cufe()).startsWith("SIM-");
    }

    @Test
    @DisplayName("reenviar la misma factura devuelve el mismo codigo")
    void deterministico() {
        Factura f = facturaDe("FV-0001");

        assertThat(simulador.enviar(f).cufe()).isEqualTo(simulador.enviar(f).cufe());
    }

    @Test
    @DisplayName("facturas distintas obtienen codigos distintos")
    void distintasFacturas() {
        assertThat(simulador.enviar(facturaDe("FV-0001")).cufe())
                .isNotEqualTo(simulador.enviar(facturaDe("FV-0002")).cufe());
    }

    @Test
    @DisplayName("el codigo tiene longitud estable")
    void longitudEstable() {
        assertThat(simulador.enviar(facturaDe("FV-0001")).cufe()).hasSize(44);
    }
}
