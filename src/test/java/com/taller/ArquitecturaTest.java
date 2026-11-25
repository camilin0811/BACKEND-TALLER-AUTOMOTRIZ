package com.taller;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * La regla de dependencias no es una intencion documentada: se verifica en
 * cada build. Si alguien importa Spring Data desde el dominio, esto falla.
 *
 * <p>Para qué sirve: Verifica en cada build la regla de dependencias entre capas (por ejemplo, que el
 * dominio no use Spring).
 * <p>PATRÓN Pruebas de arquitectura (ArchUnit): ArchUnit revisa las dependencias entre paquetes como
 *     si fuera una prueba más.
 */
@DisplayName("Reglas de arquitectura")
class ArquitecturaTest {

    private static JavaClasses clases;

    @BeforeAll
    static void importar() {
        clases = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.taller");
    }

    @Test
    @DisplayName("el dominio no depende de ninguna otra capa")
    void dominioAislado() {
        noClasses().that().resideInAPackage("com.taller.dominio..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("com.taller.aplicacion..", "com.taller.infraestructura..", "com.taller.api..")
                .because("el dominio es el centro: las dependencias apuntan hacia adentro, nunca hacia afuera")
                .check(clases);
    }

    @Test
    @DisplayName("el dominio no conoce Spring")
    void dominioSinSpring() {
        noClasses().that().resideInAPackage("com.taller.dominio..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .because("el dominio debe poder probarse y compilarse sin el framework")
                .check(clases);
    }

    @Test
    @DisplayName("el dominio no conoce MongoDB")
    void dominioSinMongo() {
        noClasses().that().resideInAPackage("com.taller.dominio..")
                .should().dependOnClassesThat().resideInAnyPackage("com.mongodb..", "org.bson..")
                .because("cambiar de base de datos no puede tocar una sola clase de dominio")
                .check(clases);
    }

    @Test
    @DisplayName("la aplicacion no depende de la infraestructura ni de la API")
    void aplicacionNoDependeDeAdaptadores() {
        noClasses().that().resideInAPackage("com.taller.aplicacion..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("com.taller.infraestructura.persistencia..", "com.taller.api..")
                .because("los casos de uso hablan con puertos, no con adaptadores")
                .check(clases);
    }

    @Test
    @DisplayName("la API no toca los documentos de MongoDB")
    void apiNoTocaDocumentos() {
        noClasses().that().resideInAPackage("com.taller.api..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.taller.infraestructura.persistencia.documento..")
                .because("exponer el documento de persistencia acopla el contrato publico al esquema")
                .check(clases);
    }

    @Test
    @DisplayName("los puertos son interfaces")
    void puertosSonInterfaces() {
        classes().that().resideInAPackage("com.taller.dominio.puerto..")
                .and().areTopLevelClasses()
                .should().beInterfaces()
                .because("un puerto es un contrato, no una implementacion")
                .check(clases);
    }

    @Test
    @DisplayName("los controladores no se saltan la capa de aplicacion")
    void controladoresUsanServicios() {
        noClasses().that().resideInAPackage("com.taller.api.controlador..")
                .should().dependOnClassesThat().resideInAPackage("com.taller.dominio.puerto..")
                .because("un controlador que llama al repositorio deja la regla de negocio sin dueno")
                .check(clases);
    }

    @Test
    @DisplayName("las capas respetan el sentido de las dependencias")
    void capasEnOrden() {
        Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("Dominio").definedBy("com.taller.dominio..")
                .layer("Aplicacion").definedBy("com.taller.aplicacion..")
                .layer("Infraestructura").definedBy("com.taller.infraestructura..")
                .layer("Api").definedBy("com.taller.api..")
                .whereLayer("Api").mayNotBeAccessedByAnyLayer()
                .whereLayer("Infraestructura").mayOnlyBeAccessedByLayers("Api")
                .whereLayer("Aplicacion").mayOnlyBeAccessedByLayers("Api", "Infraestructura")
                .check(clases);
    }

    @Test
    @DisplayName("ninguna excepcion de negocio escapa a la jerarquia TallerException")
    void excepcionesConCodigo() {
        classes().that().resideInAPackage("com.taller.dominio.excepcion..")
                .and().haveSimpleNameEndingWith("Exception")
                .and().areNotAssignableFrom(com.taller.dominio.excepcion.TallerException.class)
                .should().beAssignableTo(com.taller.dominio.excepcion.TallerException.class)
                .because("la API traduce codigos estables, y eso exige una raiz comun")
                .check(clases);
    }

    @Test
    @DisplayName("los eventos de dominio heredan de EventoDominio")
    void eventosConRaizComun() {
        classes().that().resideInAPackage("com.taller.dominio.evento..")
                .and().areTopLevelClasses()
                .should().beAssignableTo(com.taller.dominio.evento.EventoDominio.class)
                .because("el publicador despacha por tipo y necesita una raiz comun")
                .check(clases);
    }
}
