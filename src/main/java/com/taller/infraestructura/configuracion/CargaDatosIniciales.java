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
import com.taller.dominio.puerto.MecanicoRepositorio;
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

    public CargaDatosIniciales(ServicioRepositorio servicios, RepuestoRepositorio repuestos,
                               ClienteRepositorio clientes, VehiculoRepositorio vehiculos,
                               MecanicoRepositorio mecanicos, AutenticacionService autenticacion,
                               UsuarioRepositorio usuarios) {
        this.servicios = servicios;
        this.repuestos = repuestos;
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.mecanicos = mecanicos;
        this.autenticacion = autenticacion;
        this.usuarios = usuarios;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Los usuarios se revisan siempre, no solo en una base vacia: una base
        // creada antes de que existiera la autenticacion se quedaria sin
        // ninguna credencial y nadie podria entrar.
        cargarUsuarios();

        if (!servicios.listarActivos().isEmpty()) {
            log.info("El catalogo ya tiene datos; solo se verificaron los usuarios");
            return;
        }

        cargarCatalogoDeServicios();
        cargarInventario();
        String idCliente = cargarClienteConVehiculos();
        cargarPersonal();

        log.info("Carga inicial lista. Cliente de prueba: {} | placas ABC123 (automovil) y XYZ12D (motocicleta)",
                idCliente);
    }

    private void cargarCatalogoDeServicios() {
        guardarServicio("SRV-001", "Cambio de aceite y filtro", "95000", 0.8);
        guardarServicio("SRV-012", "Cambio de pastillas de freno delanteras", "120000", 1.5);
        guardarServicio("SRV-020", "Sincronizacion de motor", "210000", 2.5);
        guardarServicio("SRV-031", "Revision general de 30 puntos", "80000", 1.0);
        guardarServicio("SRV-040", "Ajuste de cadena y kit de arrastre", "75000", 1.0);
        guardarServicio("SRV-050", "Diagnostico electronico OBD-II", "60000", 0.5);
    }

    private void guardarServicio(String codigo, String descripcion, String precio, double horas) {
        servicios.guardar(new Servicio(null, codigo, descripcion, new BigDecimal(precio), horas, true));
    }

    private void cargarInventario() {
        guardarRepuesto("PAST-001", "Pastillas de freno delanteras", "90000", 10, 5);
        guardarRepuesto("FILT-009", "Filtro de aceite", "28000", 3, 6);
        guardarRepuesto("ACEI-005", "Aceite sintetico 5W-30 (litro)", "42000", 24, 10);
        guardarRepuesto("BUJI-002", "Bujia de iridio", "35000", 16, 8);
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
        creados += crearUsuario("mecanico01", claveDemo, Rol.MECANICO, "Mecanico de Prueba Uno");
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
     * Tres mecanicos, no uno.
     *
     * <p>Con un solo mecanico la demostracion se agota: al asignarlo queda
     * ocupado, y si la orden se bloquea esperando repuesto no se libera.
     */
    private void cargarPersonal() {
        mecanicos.guardar(Mecanico.nuevo("Mecanico de Prueba Uno", "20000001", "MEC-01", "frenos"));
        mecanicos.guardar(Mecanico.nuevo("Mecanico de Prueba Dos", "20000002", "MEC-02", "motor"));
        mecanicos.guardar(Mecanico.nuevo("Mecanico de Prueba Tres", "20000003", "MEC-03", "electricidad"));
    }
}
