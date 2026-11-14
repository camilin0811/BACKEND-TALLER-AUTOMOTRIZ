package com.taller.aplicacion.servicio;

import com.taller.dominio.evento.FacturaValidada;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.EstadoDian;
import com.taller.dominio.modelo.Factura;
import com.taller.dominio.modelo.MetodoPago;
import com.taller.dominio.modelo.OrdenTrabajo;
import com.taller.dominio.modelo.Pago;
import com.taller.dominio.puerto.FacturacionElectronica;
import com.taller.dominio.puerto.FacturaRepositorio;
import com.taller.dominio.puerto.GeneradorConsecutivos;
import com.taller.dominio.puerto.OrdenTrabajoRepositorio;
import com.taller.dominio.puerto.PublicadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);

    private final FacturaRepositorio facturas;
    private final OrdenTrabajoRepositorio ordenes;
    private final FacturacionElectronica proveedorDian;
    private final GeneradorConsecutivos consecutivos;
    private final PublicadorEventos publicador;
    private final BigDecimal tarifaIva;

    public FacturacionService(FacturaRepositorio facturas,
                              OrdenTrabajoRepositorio ordenes,
                              FacturacionElectronica proveedorDian,
                              GeneradorConsecutivos consecutivos,
                              PublicadorEventos publicador,
                              @Value("${taller.facturacion.iva:0.19}") BigDecimal tarifaIva) {
        this.facturas = facturas;
        this.ordenes = ordenes;
        this.proveedorDian = proveedorDian;
        this.consecutivos = consecutivos;
        this.publicador = publicador;
        this.tarifaIva = tarifaIva;
    }

    /**
     * Emite la factura de una orden cerrada y la envia a validacion.
     *
     * <p>Si la DIAN rechaza o el proveedor no responde, la factura queda
     * registrada con su estado y la orden permanece en CERRADA: nunca se
     * entrega un vehiculo con una factura que no existe legalmente.
     */
    @Transactional
    public Factura emitirPorCierre(String idOrden) {
        OrdenTrabajo orden = ordenes.buscarPorId(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("orden de trabajo", idOrden));

        Factura factura = facturas.buscarPorOrden(idOrden)
                .orElseGet(() -> Factura.emitirPara(idOrden, orden.getCliente(), orden.getTotal(), tarifaIva));
        if (factura.getNumero() == null) {
            factura.asignarNumero(consecutivos.siguienteFactura());
        }

        factura.marcarEnviada();
        FacturacionElectronica.Resultado resultado = proveedorDian.enviar(factura);

        if (resultado.validada()) {
            factura.marcarValidada(resultado.cufe());
            Factura guardada = facturas.guardar(factura);
            orden.marcarFacturada(guardada.getId(), "sistema");
            ordenes.guardar(orden);
            publicador.publicar(new FacturaValidada(guardada.getId(), idOrden, guardada.getCufe()));
            return guardada;
        }

        factura.marcarRechazada(resultado.motivoRechazo());
        log.warn("La DIAN rechazo la factura de la orden {}: {}", idOrden, resultado.motivoRechazo());
        return facturas.guardar(factura);
    }

    @Transactional
    public Factura reintentarEnvio(String idFactura) {
        Factura factura = obtener(idFactura);
        return emitirPorCierre(factura.getIdOrden());
    }

    @Transactional
    public Factura registrarPago(String idFactura, BigDecimal monto, MetodoPago metodo, String referencia) {
        Factura factura = obtener(idFactura);
        factura.registrarPago(Pago.de(monto, metodo, referencia));
        return facturas.guardar(factura);
    }

    public Factura obtener(String idFactura) {
        return facturas.buscarPorId(idFactura)
                .orElseThrow(() -> new RecursoNoEncontradoException("factura", idFactura));
    }

    public List<Factura> listarPendientesDeEnvio() {
        return facturas.listarPorEstadoDian(EstadoDian.PENDIENTE_ENVIO);
    }
}
