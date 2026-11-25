package com.taller.aplicacion.manejador;

import com.taller.aplicacion.servicio.InventarioService;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.StockBajoDetectado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * M3: dispara la reposicion cuando el stock baja del minimo.
 *
 * <p>No calcula nada: la regla de cuanto pedir vive en el dominio
 * ({@code Repuesto.cantidadReposicion}) y la creacion de la orden en
 * {@link InventarioService#generarReposicion}, el mismo camino que usa un
 * consumo rechazado por falta de stock.
 *
 * <p>Para qué sirve: M3: genera una orden de compra de reposición cuando un repuesto baja del stock
 * mínimo.
 * <p>PATRÓN Observer (publicar/suscribir): es un observador concreto: soporta() declara qué eventos le
 *     interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
 */
@Component
public class ManejadorInventario extends ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorInventario.class);

    private final InventarioService inventario;

    public ManejadorInventario(InventarioService inventario) {
        this.inventario = inventario;
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
        inventario.generarReposicion(e.getCodigoRepuesto()).ifPresentOrElse(
                compra -> log.info("Reposicion automatica: orden de compra {} por {} unidades de {}",
                        compra.getNumero(), compra.getItems().get(0).getCantidad(), e.getCodigoRepuesto()),
                () -> log.info("{} ya tiene una orden de compra abierta; no se genera otra",
                        e.getCodigoRepuesto()));
    }
}
