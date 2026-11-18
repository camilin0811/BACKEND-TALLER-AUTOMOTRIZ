package com.taller.infraestructura.configuracion;

import com.taller.dominio.modelo.Automovil;
import com.taller.dominio.modelo.Cliente;
import com.taller.dominio.modelo.Mecanico;
import com.taller.dominio.modelo.Motocicleta;
import com.taller.dominio.modelo.Proveedor;
import com.taller.dominio.modelo.Repuesto;
import com.taller.dominio.modelo.Rol;
import com.taller.dominio.modelo.Servicio;
import com.taller.dominio.puerto.ClienteRepositorio;
import com.taller.dominio.modelo.EstadoOrdenCompra;
import com.taller.dominio.puerto.MecanicoRepositorio;
import com.taller.dominio.puerto.OrdenCompraRepositorio;
import com.taller.dominio.puerto.RepuestoRepositorio;
import com.taller.dominio.puerto.ServicioRepositorio;
import com.taller.dominio.puerto.UsuarioRepositorio;
import com.taller.dominio.puerto.VehiculoRepositorio;
import com.taller.aplicacion.servicio.AutenticacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Datos de arranque para desarrollo y demostracion.
 *
 * <p>Solo se ejecuta con el perfil {@code seed} activo y nunca sobrescribe:
 * si ya hay catalogo, no toca nada. Son datos ficticios; ningun documento ni
 * telefono corresponde a una persona real.
 *
 * <pre>mvn spring-boot:run -Dspring-boot.run.profiles=seed</pre>
 */
@Component
@Profile("seed")
public class CargaDatosIniciales implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaDatosIniciales.class);

    private final ServicioRepositorio servicios;
    private final RepuestoRepositorio repuestos;
    private final ClienteRepositorio clientes;
    private final VehiculoRepositorio vehiculos;
    private final MecanicoRepositorio mecanicos;
    private final AutenticacionService autenticacion;
    private final UsuarioRepositorio usuarios;
    private final MongoTemplate mongo;
    private final OrdenCompraRepositorio ordenesCompra;

    public CargaDatosIniciales(ServicioRepositorio servicios, RepuestoRepositorio repuestos,
                               ClienteRepositorio clientes, VehiculoRepositorio vehiculos,
                               MecanicoRepositorio mecanicos, AutenticacionService autenticacion,
                               UsuarioRepositorio usuarios, MongoTemplate mongo,
                               OrdenCompraRepositorio ordenesCompra) {
        this.servicios = servicios;
        this.repuestos = repuestos;
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.mecanicos = mecanicos;
        this.autenticacion = autenticacion;
        this.usuarios = usuarios;
        this.mongo = mongo;
        this.ordenesCompra = ordenesCompra;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Los usuarios se revisan siempre, no solo en una base vacia: una base
        // creada antes de que existiera la autenticacion se quedaria sin
        // ninguna credencial y nadie podria entrar.
        cargarUsuarios();
        vincularMecanicos();
        corregirNombresSinTilde();
        corregirCatalogoSinTilde();
        recalcularBorradoresDeCompra();

        if (!servicios.listarActivos().isEmpty()) {
            log.info("El catalogo ya tiene datos; solo se verificaron los usuarios");
            return;
        }

        cargarCatalogoDeServicios();
        cargarInventario();
        String idCliente = cargarClienteConVehiculos();

        log.info("Carga inicial lista. Cliente de prueba: {} | placas ABC123 (automovil) y XYZ12D (motocicleta)",
                idCliente);
    }

    private void cargarCatalogoDeServicios() {
        guardarServicio("SRV-001", "Cambio de aceite y filtro", "95000", 0.8);
        guardarServicio("SRV-012", "Cambio de pastillas de freno delanteras", "120000", 1.5);
        guardarServicio("SRV-020", "Sincronización de motor", "210000", 2.5);
        guardarServicio("SRV-031", "Revisión general de 30 puntos", "80000", 1.0);
        guardarServicio("SRV-040", "Ajuste de cadena y kit de arrastre", "75000", 1.0);
        guardarServicio("SRV-050", "Diagnóstico electrónico OBD-II", "60000", 0.5);
    }

    private void guardarServicio(String codigo, String descripcion, String precio, double horas) {
        servicios.guardar(new Servicio(null, codigo, descripcion, new BigDecimal(precio), horas, true));
    }

    private void cargarInventario() {
        guardarRepuesto("PAST-001", "Pastillas de freno delanteras", "90000", 10, 5);
        guardarRepuesto("FILT-009", "Filtro de aceite", "28000", 3, 6);
        guardarRepuesto("ACEI-005", "Aceite sintético 5W-30 (litro)", "42000", 24, 10);
        guardarRepuesto("BUJI-002", "Bujía de iridio", "35000", 16, 8);
        guardarRepuesto("CADE-011", "Kit de arrastre para motocicleta", "180000", 4, 2);
    }

    private void guardarRepuesto(String codigo, String nombre, String precio, int stock, int minimo) {
        Repuesto repuesto = new Repuesto(null, codigo, nombre, new BigDecimal(precio), stock, minimo,
                java.util.List.of());
        repuesto.agregarProveedor(proveedorDemo().getNit());
        repuestos.guardar(repuesto);
    }

    private Proveedor proveedorDemo() {
        return new Proveedor(null, "900123456-1", "Repuestos del Sur", "+576021234567", null);
    }

    private String cargarClienteConVehiculos() {
        Cliente cliente = clientes.guardar(
                Cliente.nuevo("Cliente de Prueba", "10000001", "+573000000001",
                        "cliente.prueba@example.com", "Calle 1 # 2-3"));

        vehiculos.guardar(new Automovil(null, "ABC123", "Mazda", "3", 2019, 48500,
                "DEMOVIN0000000001", 2000, cliente.getId(), 4, "GASOLINA", 5, "AUTOMATICA"));
        vehiculos.guardar(new Motocicleta(null, "XYZ12D", "Yamaha", "FZ 2.0", 2022, 18200,
                "DEMOVIN0000000002", 150, cliente.getId(), "MONOCILINDRICO", 4, "NAKED", true));

        return cliente.getId();
    }

    /**
     * Usuarios de demostracion, uno por rol.
     *
     * <p>La clave es la misma para los tres y queda escrita aqui a proposito:
     * es un dato de desarrollo, no un secreto. En un despliegue real este
     * perfil no se activa y las credenciales se crean una a una.
     */
    private void cargarUsuarios() {
        final String claveDemo = "taller2026";
        int creados = 0;
        creados += crearUsuario("admin", claveDemo, Rol.ADMINISTRADOR, "Administrador de Prueba");
        creados += crearUsuario("recepcion01", claveDemo, Rol.RECEPCIONISTA, "Recepcionista de Prueba");
        creados += crearUsuario("mecanico01", claveDemo, Rol.MECANICO, "Mecánico de Prueba Uno");
        if (creados > 0) {
            log.info("Usuarios de demostracion creados: {} (clave: {})", creados, claveDemo);
        } else {
            log.info("Los usuarios de demostracion ya existian; no se recargo nada");
        }
    }

    /**
     * Pregunta antes de registrar en lugar de registrar y capturar el
     * duplicado: registrar calcula un hash BCrypt, que es lento a proposito,
     * y pagarlo tres veces en cada arranque solo para descubrir que los
     * usuarios ya existian alarga el despertar del servidor.
     */
    private int crearUsuario(String usuario, String clave, Rol rol, String nombre) {
        if (usuarios.buscarPorUsuario(usuario).isPresent()) {
            return 0;
        }
        try {
            autenticacion.registrar(usuario, clave, rol, nombre, null);
            return 1;
        } catch (RuntimeException e) {
            log.debug("El usuario {} ya existia: {}", usuario, e.getMessage());
            return 0;
        }
    }

    /**
     * Las bases sembradas antes de corregir las tildes guardaron "Mecanico de
     * Prueba", y el nombre tambien quedo copiado dentro de cada orden. Se
     * reemplaza una sola vez: la consulta solo encuentra los nombres viejos,
     * asi que en los arranques siguientes no actualiza nada.
     */
    private void corregirNombresSinTilde() {
        String[][] nombres = {
                {"Mecanico de Prueba", "Mecánico de Prueba"},
                {"Mecanico de Prueba Uno", "Mecánico de Prueba Uno"},
                {"Mecanico de Prueba Dos", "Mecánico de Prueba Dos"},
                {"Mecanico de Prueba Tres", "Mecánico de Prueba Tres"},
        };
        long corregidos = 0;
        for (String[] par : nombres) {
            corregidos += renombrar("empleados", "nombre", par[0], par[1]);
            corregidos += renombrar("usuarios", "nombre", par[0], par[1]);
            corregidos += renombrar("ordenes", "mecanico.nombre", par[0], par[1]);
        }
        if (corregidos > 0) {
            log.info("Se corrigieron {} nombres de demostracion sin tilde", corregidos);
        }
    }

    /**
     * Catalogo sembrado antes de corregir las tildes. El nombre tambien quedo
     * copiado en las lineas de cada orden. Como la consulta solo encuentra
     * los nombres viejos, no hace nada en los arranques siguientes.
     */
    private void corregirCatalogoSinTilde() {
        String[][] servicios = {
                {"Sincronizacion de motor", "Sincronización de motor"},
                {"Revision general de 30 puntos", "Revisión general de 30 puntos"},
                {"Diagnostico electronico OBD-II", "Diagnóstico electrónico OBD-II"},
        };
        String[][] repuestosViejos = {
                {"Aceite sintetico 5W-30 (litro)", "Aceite sintético 5W-30 (litro)"},
                {"Bujia de iridio", "Bujía de iridio"},
        };
        long corregidos = 0;
        for (String[] par : servicios) {
            corregidos += renombrar("servicios", "descripcion", par[0], par[1]);
            corregidos += renombrarEnLista("servicios", "descripcion", par[0], par[1]);
        }
        for (String[] par : repuestosViejos) {
            corregidos += renombrar("repuestos", "nombre", par[0], par[1]);
            corregidos += renombrarEnLista("repuestos", "nombre", par[0], par[1]);
        }
        if (corregidos > 0) {
            log.info("Se corrigieron {} nombres del catalogo sin tilde", corregidos);
        }
    }

    /** Renombra dentro de una lista de lineas de las ordenes de trabajo. */
    private long renombrarEnLista(String lista, String campo, String viejo, String nuevo) {
        Update cambio = new Update().set(lista + ".$[linea]." + campo, nuevo)
                .filterArray(Criteria.where("linea." + campo).is(viejo));
        return mongo.updateMulti(Query.query(Criteria.where(lista + "." + campo).is(viejo)),
                cambio, "ordenes").getModifiedCount();
    }

    /**
     * Las ordenes de compra en borrador creadas con la regla anterior podian
     * pedir la cantidad de un consumo rechazado (la OC-0001 pedia 99 filtros
     * con stock 3 y minimo 6). Se recalculan una sola vez con la regla actual,
     * (minimo x 2) - stock. Despues no se vuelven a tocar: desde ahora un
     * borrador se puede editar a mano y recalcularlo pisaria esa decision.
     */
    private void recalcularBorradoresDeCompra() {
        final String marca = "reposicion-regla-doble-minimo";
        if (mongo.exists(Query.query(Criteria.where("_id").is(marca)), "migraciones")) {
            return;
        }
        int recalculadas = 0;
        for (var compra : ordenesCompra.listar()) {
            if (compra.getEstado() != EstadoOrdenCompra.BORRADOR) {
                continue;
            }
            for (var item : compra.getItems()) {
                var repuesto = repuestos.buscarPorCodigo(item.getCodigoRepuesto()).orElse(null);
                if (repuesto != null && item.getCantidad() != repuesto.cantidadReposicion()) {
                    log.info("Borrador {}: {} pasa de {} a {} unidades", compra.getNumero(),
                            item.getCodigoRepuesto(), item.getCantidad(), repuesto.cantidadReposicion());
                    compra.ajustarCantidad(item.getCodigoRepuesto(), repuesto.cantidadReposicion());
                    recalculadas++;
                }
            }
            ordenesCompra.guardar(compra);
        }
        mongo.insert(new org.bson.Document("_id", marca)
                .append("aplicada", java.util.Date.from(java.time.Instant.now()))
                .append("borradoresRecalculados", recalculadas), "migraciones");
    }

    private long renombrar(String coleccion, String campo, String viejo, String nuevo) {
        return mongo.updateMulti(Query.query(Criteria.where(campo).is(viejo)),
                Update.update(campo, nuevo), coleccion).getModifiedCount();
    }

    /**
     * Tres mecanicos, cada uno con su usuario y su ficha de empleado
     * vinculados (el usuario guarda el id de la ficha en idPersona).
     *
     * <p>El selector de mecanico de una orden lista los usuarios con rol
     * mecanico, y la orden guarda la ficha: sin el vinculo nadie se podria
     * asignar. Con un solo mecanico la demostracion ademas se agota: al
     * asignarlo queda ocupado. Es idempotente: busca la ficha por documento
     * y el usuario por nombre de usuario antes de crear nada.
     */
    private void vincularMecanicos() {
        String[][] equipo = {
                {"mecanico01", "Mecánico de Prueba Uno", "20000001", "MEC-01", "frenos"},
                {"mecanico02", "Mecánico de Prueba Dos", "20000002", "MEC-02", "motor"},
                {"mecanico03", "Mecánico de Prueba Tres", "20000003", "MEC-03", "electricidad"},
        };
        var fichas = mecanicos.listar();
        for (String[] m : equipo) {
            Mecanico ficha = fichas.stream().filter(f -> m[2].equals(f.getDocumento())).findFirst()
                    .orElseGet(() -> mecanicos.guardar(Mecanico.nuevo(m[1], m[2], m[3], m[4])));
            var usuario = usuarios.buscarPorUsuario(m[0]);
            if (usuario.isEmpty()) {
                autenticacion.registrar(m[0], "taller2026", Rol.MECANICO, m[1], ficha.getId());
                log.info("Mecánico de demostración {} creado y vinculado a {}", m[0], ficha.getCodigo());
            } else if (usuario.get().getIdPersona() == null) {
                mongo.updateFirst(Query.query(Criteria.where("usuario").is(m[0])),
                        Update.update("idPersona", ficha.getId()), "usuarios");
                log.info("Usuario {} vinculado a la ficha {}", m[0], ficha.getCodigo());
            }
        }
    }
}
