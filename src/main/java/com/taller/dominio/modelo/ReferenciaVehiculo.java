package com.taller.dominio.modelo;

/**
 * Para qué sirve: Copia del vehículo (id, placa, tipo y descripción) guardada dentro de la orden.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 * <p>PATRÓN Agregado y raíz de agregado (DDD): desnormalización deliberada: la orden guarda estos
 *     datos tal como estaban ese día, sin leer otra colección.
 */
public record ReferenciaVehiculo(String id, String placa, TipoVehiculo tipo, String descripcion) {
}
