package com.taller.infraestructura.integracion;

import com.taller.dominio.modelo.Factura;
import com.taller.dominio.puerto.FacturacionElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Facturacion electronica simulada.
 *
 * <p>Es el adaptador activo mientras no haya un proveedor tecnologico
 * contratado. Genera un CUFE con la misma forma que el real —un resumen
 * SHA-256 de los datos de la factura— de modo que el ciclo completo se puede
 * recorrer sin depender de nadie.
 *
 * <p><strong>No tiene validez fiscal.</strong> El CUFE que devuelve no lo
 * emitio la DIAN y no sirve para una factura de verdad. Por eso cada emision
 * queda registrada con una advertencia explicita en el log: si alguien pone
 * esto en produccion por descuido, el registro lo delata.
 *
 * <p>Para usar el proveedor real basta configurar
 * {@code taller.facturacion.simulada=false} y las credenciales; entonces se
 * activa {@link DianFacturacionClient} en su lugar. El dominio no se entera:
 * los dos implementan el mismo puerto.
 *
 * <p>Para qué sirve: Facturación electrónica simulada para desarrollo y demostración: genera un CUFE
 * con la forma del real, sin validez fiscal.
 * <p>PATRÓN Strategy: estrategia de facturación activa por defecto; se cambia por
 *     DianFacturacionClient sin tocar el dominio.
 * <p>PATRÓN Adapter: implementa el puerto FacturacionElectronica.
 */
@Component
// PATRÓN Strategy: implementación por defecto de FacturacionElectronica, elegida por configuración.
@ConditionalOnProperty(name = "taller.facturacion.simulada", havingValue = "true", matchIfMissing = true)
public class FacturacionSimulada implements FacturacionElectronica {

    private static final Logger log = LoggerFactory.getLogger(FacturacionSimulada.class);

    @Override
    public Resultado enviar(Factura factura) {
        log.warn("FACTURACION SIMULADA: la factura {} NO fue validada por la DIAN. "
                 + "El CUFE generado no tiene validez fiscal.", factura.getNumero());

        return Resultado.validada(cufeSimulado(factura));
    }

    /**
     * Resumen determinista de los datos de la factura.
     *
     * <p>Determinista a proposito: reenviar la misma factura devuelve el mismo
     * codigo, igual que haria la DIAN, en vez de un valor distinto cada vez.
     */
    private String cufeSimulado(Factura factura) {
        String semilla = "%s|%s|%s|%s".formatted(
                factura.getNumero(),
                factura.getIdOrden(),
                factura.getTotal(),
                factura.getFecha());
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256")
                    .digest(semilla.getBytes(StandardCharsets.UTF_8));
            return "SIM-" + HexFormat.of().formatHex(resumen).substring(0, 40);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 deberia existir en cualquier JVM", e);
        }
    }
}
