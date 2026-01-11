package org.acme.pedidos.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Health check para verificar conectividade com Kafka.
 * Usado no readiness probe.
 */
@Readiness
@ApplicationScoped
public class KafkaHealthCheck implements HealthCheck {

    private static final Logger LOGGER = Logger.getLogger(KafkaHealthCheck.class);

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Kafka connection health check");
        try {
            // Tenta criar um AdminClient temporário e listar tópicos
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("request.timeout.ms", "5000");
            props.put("connections.max.idle.ms", "10000");

            try (var adminClient = org.apache.kafka.clients.admin.AdminClient.create(props)) {
                // Tenta listar tópicos com timeout de 5s
                adminClient.listTopics()
                        .listings()
                        .get(5, TimeUnit.SECONDS);

                return builder
                        .up()
                        .withData("bootstrap.servers", bootstrapServers)
                        .build();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Health check Kafka interrompido", e);
            return builder
                    .down()
                    .withData("error", "Interrupted")
                    .withData("bootstrap.servers", bootstrapServers)
                    .build();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.warnf("Kafka não está acessível: %s", e.getMessage());
            return builder
                    .down()
                    .withData("error", "Cannot connect to Kafka")
                    .withData("bootstrap.servers", bootstrapServers)
                    .build();
        } catch (Exception e) {
            LOGGER.errorf(e, "Erro ao verificar saúde do Kafka");
            return builder
                    .down()
                    .withData("error", e.getMessage())
                    .withData("bootstrap.servers", bootstrapServers)
                    .build();
        }
    }
}
