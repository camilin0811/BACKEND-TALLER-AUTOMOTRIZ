package com.taller.dominio.evento;

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
