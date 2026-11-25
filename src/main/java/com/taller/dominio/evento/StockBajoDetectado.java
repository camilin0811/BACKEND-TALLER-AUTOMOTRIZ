package com.taller.dominio.evento;

/**
 * Para qué sirve: Un repuesto quedó por debajo de su stock mínimo. Lo publica InventarioService; lo
 * escucha M3 (reposición).
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
public class StockBajoDetectado extends EventoDominio {

    private final String codigoRepuesto;
    private final int stockActual;
    private final int stockMinimo;

    public StockBajoDetectado(String codigoRepuesto, int stockActual, int stockMinimo) {
        this.codigoRepuesto = codigoRepuesto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    public String getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public int getStockActual() {
        return stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
}
