package org.acme.pedidos.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class PedidoService {

    private static final Logger LOOGER = Logger.getLogger(PedidoService.class);

    @Inject
    MeterRegistry meterRegistry;

    private Counter contadorPedidosCriados;
    private Counter contadorPublicacaoKafkaSucesso;
    private Counter contadorPublicacaoKafkaErro;
    private Timer tempoProcessamentoPedido;

    @PostConstruct
    void init() {

        // ininializa métricas customizadas
        contadorPedidosCriados = meterRegistry.counter("orders.created.total",
                "type", "order_request");
        contadorPublicacaoKafkaSucesso = meterRegistry.counter("kafka.publish.success.total",
                "topic", "orders.received");
        contadorPublicacaoKafkaErro = meterRegistry.counter("kafka.publish.error.total",
                "topic", "orders.received");
        tempoProcessamentoPedido = meterRegistry.timer("orders.processing.duration",
                "endpoint", "POST_/api/v1/orders");

    }
}
