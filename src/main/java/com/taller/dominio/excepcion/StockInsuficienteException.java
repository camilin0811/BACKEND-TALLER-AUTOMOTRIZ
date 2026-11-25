package com.taller.dominio.excepcion;

/**
 * Para qué sirve: No hay unidades suficientes del repuesto. ManejadorGlobalErrores la responde con
 * 409.
 * <p>PATRÓN Herencia y polimorfismo: hereda de TallerException; el tipo de la excepción decide el
 *     código HTTP, sin if en los controladores.
 */
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
