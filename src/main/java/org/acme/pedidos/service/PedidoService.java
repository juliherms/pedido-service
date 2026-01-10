package org.acme.pedidos.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.pedidos.dto.request.CriarPedidoRequest;
import org.acme.pedidos.dto.request.ItemPedidoRequest;
import org.acme.pedidos.dto.response.PedidoCriadoResponse;
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

    public Uni<PedidoCriadoResponse> createOrder(CriarPedidoRequest request) {

        Timer.Sample sample = Timer.start(meterRegistry);

        String idPedido = UUID.randomUUID().toString();
        Instant criadoEm = Instant.now();

        // Calcula totais
        int totalItems = calculaarTotalItens(request);
        BigDecimal totalAmount = calculateValorTotal(request);

        LOOGER.infof("Totais calculados. orderId=%s, totalItems=%d, totalAmount=%s ",
                idPedido, totalItems, totalAmount);

        // TODO: criar o evento kafka

        return null;
    }

    /**
     * Calcula o total de itens no pedido.
     */
    private int calculaarTotalItens(CriarPedidoRequest request) {
        return request.itens().stream()
                .mapToInt(ItemPedidoRequest::quantidade)
                .sum();
    }

    /**
     * Calcula o valor total do pedido com BigDecimal (scale 2, HALF_UP).
     */
    private BigDecimal calculateValorTotal(CriarPedidoRequest request) {
        return request.itens().stream()
                .map(item -> {
                    BigDecimal quantity = BigDecimal.valueOf(item.quantidade());
                    return item.valorUnitario().multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
