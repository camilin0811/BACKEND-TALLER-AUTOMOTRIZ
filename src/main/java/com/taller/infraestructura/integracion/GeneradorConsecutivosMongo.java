package com.taller.infraestructura.integracion;

import com.taller.dominio.puerto.GeneradorConsecutivos;
import com.taller.infraestructura.persistencia.documento.ConsecutivoDoc;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Consecutivos de negocio con un contador atomico por tipo.
 *
 * <p>findAndModify con upsert garantiza que dos recepciones simultaneas no
 * obtengan el mismo numero de orden.
 */
@Component
public class GeneradorConsecutivosMongo implements GeneradorConsecutivos {

    private final MongoTemplate mongo;

    public GeneradorConsecutivosMongo(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public String siguienteOrdenTrabajo() {
        return formatear("OT", siguiente("ordenTrabajo"));
    }

    @Override
    public String siguienteCotizacion() {
        return formatear("COT", siguiente("cotizacion"));
    }

    @Override
    public String siguienteFactura() {
        return formatear("FV", siguiente("factura"));
    }

    @Override
    public String siguienteOrdenCompra() {
        return formatear("OC", siguiente("ordenCompra"));
    }

    @Override
    public String siguienteCita() {
        return formatear("CIT", siguiente("cita"));
    }

    private long siguiente(String tipo) {
        ConsecutivoDoc doc = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(tipo)),
                new Update().inc("valor", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                ConsecutivoDoc.class);
        return doc == null ? 1L : doc.valor;
    }

    private String formatear(String prefijo, long valor) {
        return "%s-%04d".formatted(prefijo, valor);
    }
}
