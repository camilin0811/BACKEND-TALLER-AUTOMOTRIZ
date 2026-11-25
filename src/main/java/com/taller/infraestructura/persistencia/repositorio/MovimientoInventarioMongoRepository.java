package com.taller.infraestructura.persistencia.repositorio;

import com.taller.infraestructura.persistencia.documento.MovimientoInventarioDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Para qué sirve: Consultas de Spring Data sobre la colección "movimientos_inventario".
 * <p>PATRÓN Repository de Spring Data (proxy dinámico): interfaz declarativa: Spring genera la
 *     implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
 */
public interface MovimientoInventarioMongoRepository extends MongoRepository<MovimientoInventarioDoc, String> {

    java.util.List<MovimientoInventarioDoc> findByCodigoRepuestoOrderByFechaDesc(String codigoRepuesto);
}
