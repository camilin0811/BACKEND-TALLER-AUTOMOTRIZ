# =============================================================================
#  Imagen del backend del taller
#
#  Dos etapas: la primera compila con el JDK completo, la segunda solo lleva
#  el JRE y el jar. La imagen final no contiene ni Maven, ni el codigo fuente,
#  ni el repositorio de dependencias.
# =============================================================================

# ------------------------------------------------------------------ compilar
FROM maven:3.9-eclipse-temurin-21 AS construccion

WORKDIR /origen

# El pom se copia solo primero para que la capa de dependencias quede en cache:
# un cambio en el codigo no obliga a descargar Maven Central otra vez.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ------------------------------------------------------------------ ejecutar
FROM eclipse-temurin:21-jre-alpine AS ejecucion

# Usuario sin privilegios: si alguien escapa del proceso, no es root.
RUN addgroup -S taller && adduser -S taller -G taller

WORKDIR /app
COPY --from=construccion /origen/target/*.jar app.jar
RUN chown -R taller:taller /app

USER taller

EXPOSE 8080

# El contenedor se declara sano solo cuando el actuator responde, no cuando el
# proceso arranca: sin MongoDB la aplicacion no sirve de nada.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# MaxRAMPercentage en vez de -Xmx: la JVM respeta el limite de memoria que le
# ponga el orquestador en lugar de un valor fijo que puede no caber.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
