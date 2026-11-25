package com.taller.dominio.modelo;

/**
 * Copia parcial del cliente dentro de la orden.
 *
 * <p>Es desnormalizacion deliberada: evita una lectura extra para pintar el
 * tablero. Una orden es un documento historico, asi que conservar el telefono
 * que el cliente tenia ese dia es correcto, no un error.
 *
 * <p>Para qué sirve: Copia del cliente (id, nombre y teléfono) guardada dentro de la orden.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 * <p>PATRÓN Agregado y raíz de agregado (DDD): desnormalización deliberada: la orden guarda estos
 *     datos tal como estaban ese día, sin leer otra colección.
 */
public record ReferenciaCliente(String id, String nombre, String telefono) {
}
