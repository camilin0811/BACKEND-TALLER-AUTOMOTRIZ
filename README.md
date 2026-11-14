# Sistema de Gestión del Taller Automotriz Automatizado

Backend del sistema de gestión para un taller de automóviles y motocicletas.

**Toda la documentación de diseño está en la [wiki del repositorio](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki).**

| | |
|---|---|
| Arquitectura | [Las cinco vistas](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/02-Arquitectura) |
| Dominio | [20 entidades e invariantes](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/03-Modelo-de-Dominio) |
| API | [Contrato REST](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/07-API-REST) |
| Datos | [Modelo MongoDB](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/08-Modelo-de-Datos-MongoDB) |
| Decisiones | [10 ADR](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/09-Decisiones-de-Arquitectura) |
| Pruebas | [Estrategia](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/11-Estrategia-de-Pruebas) |

## Requisitos

- Java 21 o superior
- Maven 3.9+
- MongoDB (local o Atlas)

## Ejecutar

```bash
cp src/main/resources/application-example.yml src/main/resources/application-local.yml
# completar los valores en application-local.yml
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

La API queda en `http://localhost:8080/api/v1` y la documentación interactiva en `http://localhost:8080/api-docs`.

## Pruebas

```bash
mvn test              # unitarias del dominio
mvn verify            # + cobertura mínima del dominio (90 %)
```

## Arquitectura

Hexagonal. La regla que no se negocia: **el dominio no depende de nada**.

```
com.taller
├── dominio          entidades, eventos, puertos. Java puro, sin Spring.
├── aplicacion       casos de uso y manejadores de eventos
├── infraestructura  adaptadores: MongoDB, WhatsApp, DIAN, pasarela, OBD-II
└── api              controladores REST, DTO y mapeadores
```

Esa regla no está solo documentada: la verifica `ArquitecturaTest` con ArchUnit en cada build.
