# Sistema de Gestión del Taller Automotriz Automatizado

Backend del sistema de gestión para un taller de **automóviles y motocicletas**.

**Todo el diseño está en la [wiki del repositorio](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki).** El código implementa lo que allí se decidió; si los dos se contradicen, gana la wiki y el código es el que tiene el error.

| | |
|---|---|
| Arquitectura | [Las cinco vistas](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/02-Arquitectura) |
| Dominio | [20 entidades e invariantes](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/03-Modelo-de-Dominio) |
| Automatización | [Los cuatro motores](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/04-Motores-de-Automatizacion) |
| API | [Contrato REST](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/07-API-REST) |
| Datos | [Modelo MongoDB](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/08-Modelo-de-Datos-MongoDB) |
| Decisiones | [10 ADR](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/09-Decisiones-de-Arquitectura) |
| Pruebas | [Estrategia](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/wiki/11-Estrategia-de-Pruebas) |

Las [issues](https://github.com/camilin0811/TALLER-AUTOMOTRIZ/issues) son las 20 historias de usuario, cada una con su mockup y sus criterios de aceptación.

## Requisitos

- **Java 21** o superior
- **Maven 3.9+**
- **MongoDB** en ejecución (local o Atlas). La aplicación crea los índices al arrancar, así que no inicia sin base de datos.

## Ejecutar

```bash
# 1. Levantar MongoDB
docker run -d --name mongo-taller -p 27017:27017 mongo:7

# 2. Configurar el entorno
cp src/main/resources/application-example.yml src/main/resources/application-local.yml
#    completar los valores en application-local.yml

# 3. Arrancar
mvn spring-boot:run
```

| Recurso | URL |
|---|---|
| API | `http://localhost:8080/api/v1` |
| Documentación interactiva | `http://localhost:8080/api-docs` |
| Estado del servicio | `http://localhost:8080/actuator/health` |

## Pruebas

```bash
mvn test      # 193 pruebas unitarias del dominio y de arquitectura
mvn verify    # lo anterior + puerta de cobertura (90 % mínimo en el dominio)
```

La cobertura actual del dominio es del **96,6 %**. Si baja del 90 %, el build falla: no es una recomendación, es una puerta.

## Arquitectura

Hexagonal. La regla que no se negocia: **el dominio no depende de nada**.

```
com.taller
├── dominio          entidades, eventos y puertos. Java puro, sin Spring ni Mongo.
├── aplicacion       casos de uso y los cuatro manejadores de eventos
├── infraestructura  adaptadores: MongoDB, WhatsApp, DIAN, pasarela, OBD-II
└── api              controladores REST, DTO y mapeadores
```

Esa regla no está solo documentada: `ArquitecturaTest` la verifica con ArchUnit en cada build. Si alguien importa Spring desde el dominio, el build falla.

### Dos piezas que conviene entender antes de tocar el código

**La máquina de estados.** `EstadoOrden` declara las once transiciones válidas y es la única vía para cambiar el estado de una orden. Un salto ilegal lanza `TransicionInvalidaException`, cuyo mensaje enumera las transiciones posibles desde el estado actual.

**Los eventos de dominio.** `OrdenTrabajo` acumula eventos; el servicio los publica **después** de persistir, para que ningún evento salga por un cambio que no quedó guardado. Agregar una consecuencia nueva al negocio es escribir un `ManejadorEventos` más, nunca modificar el servicio que lo emite.

## Lo que falta

- **Autenticación.** No hay Spring Security todavía. La API está abierta; el `AuthController` del diseño no está implementado.
- **Pruebas de integración** con Testcontainers contra MongoDB real.
- **Pruebas de aceptación** con Cucumber sobre los escenarios Gherkin de las historias.
