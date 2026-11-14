package com.taller.dominio.excepcion;

public class StockInsuficienteException extends TallerException {

    private final String codigoRepuesto;
    private final int disponible;
    private final int solicitado;

    public StockInsuficienteException(String codigoRepuesto, int disponible, int solicitado) {
        super("STOCK_INSUFICIENTE",
                "El repuesto %s tiene %d unidad(es) disponible(s) y se solicitaron %d"
                        .formatted(codigoRepuesto, disponible, solicitado));
        this.codigoRepuesto = codigoRepuesto;
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public String getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public int getDisponible() {
        return disponible;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
