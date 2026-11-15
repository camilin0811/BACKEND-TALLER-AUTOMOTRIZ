package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.modelo.Rol.Permiso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Roles y usuarios")
class SeguridadTest {

    @Nested
    @DisplayName("Matriz de permisos")
    class Permisos {

        @Test
        @DisplayName("el cliente solo ve lo suyo y aprueba cotizaciones")
        void cliente() {
            assertThat(Rol.CLIENTE.puede(Permiso.VER_ORDENES_PROPIAS)).isTrue();
            assertThat(Rol.CLIENTE.puede(Permiso.APROBAR_COTIZACION)).isTrue();
            assertThat(Rol.CLIENTE.puede(Permiso.VER_FACTURAS_PROPIAS)).isTrue();

            assertThat(Rol.CLIENTE.puede(Permiso.VER_TODAS_LAS_ORDENES)).isFalse();
            assertThat(Rol.CLIENTE.puede(Permiso.LEER_INVENTARIO)).isFalse();
            assertThat(Rol.CLIENTE.puede(Permiso.VER_REPORTES)).isFalse();
        }

        @Test
        @DisplayName("el mecanico consume inventario pero no factura")
        void mecanico() {
            assertThat(Rol.MECANICO.puede(Permiso.VER_ORDENES_ASIGNADAS)).isTrue();
            assertThat(Rol.MECANICO.puede(Permiso.CONSUMIR_INVENTARIO)).isTrue();
            assertThat(Rol.MECANICO.puede(Permiso.CERRAR_ORDEN)).isTrue();

            assertThat(Rol.MECANICO.puede(Permiso.ESCRIBIR_FACTURAS)).isFalse();
            assertThat(Rol.MECANICO.puede(Permiso.LEER_FACTURAS)).isFalse();
            assertThat(Rol.MECANICO.puede(Permiso.ESCRIBIR_INVENTARIO)).isFalse();
        }

        @Test
        @DisplayName("el recepcionista gestiona ordenes y factura, pero no edita el catalogo")
        void recepcionista() {
            assertThat(Rol.RECEPCIONISTA.puede(Permiso.VER_TODAS_LAS_ORDENES)).isTrue();
            assertThat(Rol.RECEPCIONISTA.puede(Permiso.ESCRIBIR_FACTURAS)).isTrue();
            assertThat(Rol.RECEPCIONISTA.puede(Permiso.LEER_CATALOGO)).isTrue();

            assertThat(Rol.RECEPCIONISTA.puede(Permiso.ESCRIBIR_CATALOGO)).isFalse();
            assertThat(Rol.RECEPCIONISTA.puede(Permiso.ESCRIBIR_INVENTARIO)).isFalse();
            assertThat(Rol.RECEPCIONISTA.puede(Permiso.VER_REPORTES)).isFalse();
        }

        @Test
        @DisplayName("solo el administrador ve reportes y edita el catalogo")
        void administrador() {
            assertThat(Rol.ADMINISTRADOR.puede(Permiso.VER_REPORTES)).isTrue();
            assertThat(Rol.ADMINISTRADOR.puede(Permiso.ESCRIBIR_CATALOGO)).isTrue();
            assertThat(Rol.ADMINISTRADOR.puede(Permiso.ESCRIBIR_INVENTARIO)).isTrue();

            for (Rol otro : new Rol[] { Rol.CLIENTE, Rol.MECANICO, Rol.RECEPCIONISTA }) {
                assertThat(otro.puede(Permiso.VER_REPORTES))
                        .as("%s no deberia ver reportes", otro).isFalse();
                assertThat(otro.puede(Permiso.ESCRIBIR_CATALOGO))
                        .as("%s no deberia editar el catalogo", otro).isFalse();
            }
        }

        @ParameterizedTest
        @EnumSource(Rol.class)
        @DisplayName("todo rol declara al menos un permiso y expone su autoridad")
        void todoRolEsUtil(Rol rol) {
            assertThat(rol.permisos()).isNotEmpty();
            assertThat(rol.autoridad()).isEqualTo("ROLE_" + rol.name());
        }

        @ParameterizedTest
        @EnumSource(Rol.class)
        @DisplayName("ningun rol salvo el administrador acumula todos los permisos")
        void soloElAdminLoPuedeTodo(Rol rol) {
            boolean todos = rol.permisos().size() == Permiso.values().length;
            assertThat(todos).isFalse();
        }
    }

    @Nested
    @DisplayName("Usuario")
    class Usuarios {

        private Usuario activo() {
            return Usuario.nuevo("recepcion01", "$2a$10$hashficticio", Rol.RECEPCIONISTA,
                    "Marta Rivas", "emp-1");
        }

        @Test
        @DisplayName("el nombre de usuario se normaliza a minusculas")
        void normalizacion() {
            Usuario u = Usuario.nuevo("  Recepcion01  ", "hash", Rol.RECEPCIONISTA, "Marta", null);
            assertThat(u.getUsuario()).isEqualTo("recepcion01");
        }

        @Test
        @DisplayName("exige usuario, hash y rol")
        void camposObligatorios() {
            assertThatThrownBy(() -> Usuario.nuevo(" ", "hash", Rol.CLIENTE, null, null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Usuario.nuevo("juan", "", Rol.CLIENTE, null, null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Usuario.nuevo("juan", "hash", null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("un usuario desactivado no puede entrar")
        void desactivado() {
            Usuario u = activo();
            u.desactivar();

            assertThat(u.isActivo()).isFalse();
            assertThatThrownBy(u::exigirActivo)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("desactivado");
        }

        @Test
        @DisplayName("un usuario desactivado pierde todos sus permisos")
        void desactivadoSinPermisos() {
            Usuario u = activo();
            assertThat(u.puede(Permiso.VER_TODAS_LAS_ORDENES)).isTrue();

            u.desactivar();
            assertThat(u.puede(Permiso.VER_TODAS_LAS_ORDENES)).isFalse();
        }

        @Test
        @DisplayName("registra el ultimo acceso")
        void ultimoAcceso() {
            Usuario u = activo();
            assertThat(u.getUltimoAcceso()).isNull();

            u.registrarAcceso();
            assertThat(u.getUltimoAcceso()).isNotNull();
        }

        @Test
        @DisplayName("el cambio de clave reemplaza el hash y rechaza vacios")
        void cambioDeClave() {
            Usuario u = activo();
            u.cambiarClave("$2a$10$otrohash");
            assertThat(u.getHashClave()).isEqualTo("$2a$10$otrohash");

            assertThatThrownBy(() -> u.cambiarClave("  "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("el texto del usuario nunca incluye el hash")
        void noFiltraElHash() {
            Usuario u = activo();
            assertThat(u.toString())
                    .contains("recepcion01")
                    .contains("RECEPCIONISTA")
                    .doesNotContain(u.getHashClave());
        }

        @Test
        @DisplayName("conserva el enlace con la persona y el identificador")
        void datos() {
            Usuario u = activo();
            u.asignarId("usr-1");

            assertThat(u.getId()).isEqualTo("usr-1");
            assertThat(u.getIdPersona()).isEqualTo("emp-1");
            assertThat(u.getNombre()).isEqualTo("Marta Rivas");
            assertThat(u.getRol()).isEqualTo(Rol.RECEPCIONISTA);
        }
    }
}
