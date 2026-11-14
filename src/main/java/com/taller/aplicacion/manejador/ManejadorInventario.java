package com.taller.aplicacion.manejador;

import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.StockBajoDetectado;
import com.taller.dominio.modelo.OrdenCompra;
import com.taller.dominio.modelo.Repuesto;
import com.taller.dominio.puerto.GeneradorConsecutivos;
import com.taller.dominio.puerto.OrdenCompraRepositorio;
import com.taller.dominio.puerto.RepuestoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** M3: mantiene el stock coherente y dispara la reposicion. */
@Component
public class ManejadorInventario extends ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorInventario.class);

    private final RepuestoRepositorio repuestos;
    private final OrdenCompraRepositorio ordenesCompra;
    private final GeneradorConsecutivos consecutivos;

    public ManejadorInventario(RepuestoRepositorio repuestos, OrdenCompraRepositorio ordenesCompra,
                               GeneradorConsecutivos consecutivos) {
        this.repuestos = repuestos;
        this.ordenesCompra = ordenesCompra;
        this.consecutivos = consecutivos;
    }

    @Override
    public boolean soporta(String tipoEvento) {
        return "StockBajoDetectado".equals(tipoEvento);
    }

    @Override
    public void manejar(EventoDominio evento) {
        if (!(evento instanceof StockBajoDetectado e)) {
            return;
        }
        Repuesto repuesto = repuestos.buscarPorCodigo(e.getCodigoRepuesto()).orElse(null);
        if (repuesto == null) {
            log.warn("Stock bajo de un repuesto inexistente: {}", e.getCodigoRepuesto());
            return;
        }
        int cantidad = Math.max(repuesto.getStockMinimo() * 2, 1);
        OrdenCompra compra = OrdenCompra.borradorPorReposicion(
                repuesto.proveedorHabitual(), repuesto.getCodigo(), cantidad, repuesto.getPrecio());
        compra.asignarNumero(consecutivos.siguienteOrdenCompra());
        ordenesCompra.guardar(compra);
        log.info("Reposicion automatica: orden de compra {} por {} unidades de {}",
                compra.getNumero(), cantidad, repuesto.getCodigo());
    }
}
