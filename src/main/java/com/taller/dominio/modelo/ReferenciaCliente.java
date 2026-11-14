package com.taller.dominio.modelo;

/**
 * Copia parcial del cliente dentro de la orden.
 *
 * <p>Es desnormalizacion deliberada: evita una lectura extra para pintar el
 * tablero. Una orden es un documento historico, asi que conservar el telefono
 * que el cliente tenia ese dia es correcto, no un error.
 */
public record ReferenciaCliente(String id, String nombre, String telefono) {
}
