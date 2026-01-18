# ==================================================================
# Dockerfile Multi-Stage para Quarkus 3.x (JVM Mode)
# ==================================================================
# Stage 1: Build da aplicação com Maven
# Stage 2: Runtime otimizado com JRE

# ==================================================================
# Stage 1: BUILD
# ==================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

LABEL stage=build
LABEL description="Build stage para compilação da aplicação Quarkus"

WORKDIR /build

# Copiar apenas pom.xml primeiro para cache de dependências
COPY pom.xml .

# Download de dependências (layer cacheável)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (skip tests para build mais rápido)
# Gera quarkus-app/ em target/
RUN mvn clean package -DskipTests -B

# ==================================================================
# Stage 2: RUNTIME
# ==================================================================
FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="Exemplo Orders Team"
LABEL app="ms-pedidos-api"
LABEL version="1.0.0"

# Variáveis de ambiente
ENV LANGUAGE='en_US:en'
ENV LANG='en_US.UTF-8'
ENV TZ='America/Sao_Paulo'

# Criar usuário não-root para segurança
RUN groupadd -r quarkus -g 1001 && \
    useradd -u 1001 -r -g quarkus -m -d /app -s /sbin/nologin -c "Quarkus user" quarkus

WORKDIR /app

# Copiar artefatos do build
# Quarkus 3.x gera estrutura em target/quarkus-app/
COPY --from=build --chown=quarkus:quarkus /build/target/quarkus-app/lib/ ./lib/
COPY --from=build --chown=quarkus:quarkus /build/target/quarkus-app/*.jar ./
COPY --from=build --chown=quarkus:quarkus /build/target/quarkus-app/app/ ./app/
COPY --from=build --chown=quarkus:quarkus /build/target/quarkus-app/quarkus/ ./quarkus/

# Mudar para usuário não-root
USER quarkus

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD java -jar /app/quarkus-run.jar --version || exit 1

# Configurações de JVM otimizadas para container
# -XX:MaxRAMPercentage: usa % da memória do container
# -XX:+UseContainerSupport: detecta limites do container
# -XX:+ExitOnOutOfMemoryError: mata processo em OOM (k8s restart)
# -Djava.util.logging.manager: LogManager do Quarkus
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
               -Dquarkus.http.host=0.0.0.0"

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/quarkus-run.jar"]


