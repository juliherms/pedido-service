package org.acme.pedidos.producer;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.pedidos.dto.event.PedidoRecebidoEvent;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Producer responsável por publicar eventos de pedidos no Kafka.
 */
@ApplicationScoped
public class PedidoEventProducer {

    private static final Logger LOGGER = Logger.getLogger(PedidoEventProducer.class);
    private static final String EVENT_TYPE_HEADER = "eventType";
    private static final String SCHEMA_VERSION_HEADER = "schemaVersion";
    private static final String EVENT_TYPE_VALUE = "OrderReceived";
    private static final String SCHEMA_VERSION_VALUE = "1";

    @Inject
    @Channel("orders-out")
    Emitter<Record<String, PedidoRecebidoEvent>> emitter;

    public Uni<org.apache.kafka.clients.producer.RecordMetadata> publicar(
            String orderId,
            PedidoRecebidoEvent event
    ) {
        LOGGER.infof("Publicando evento OrderReceived. orderId=%s, eventId=%s",
                orderId, event.eventId());

        // Cria headers customizados
        var kafkaMetadata = OutgoingKafkaRecordMetadata.<String>builder()
                .withHeaders(List.of(
                        new RecordHeader(EVENT_TYPE_HEADER, EVENT_TYPE_VALUE.getBytes(StandardCharsets.UTF_8)),
                        new RecordHeader(SCHEMA_VERSION_HEADER, SCHEMA_VERSION_VALUE.getBytes(StandardCharsets.UTF_8))
                ))
                .build();

        // Cria record Kafka com key=orderId e value=event
        Record<String, PedidoRecebidoEvent> record = Record.of(orderId, event);

        // Cria mensagem com metadata
        Message<Record<String, PedidoRecebidoEvent>> message = Message.of(record)
                .addMetadata(kafkaMetadata);

        // Envia mensagem e retorna Uni que completa quando o broker confirma (ack)
        emitter.send(message);

        // Retorna um Uni vazio que representa sucesso
        return Uni.createFrom().item(() -> {
                    LOGGER.infof("Evento publicado com sucesso. orderId=%s", orderId);
                    // Retorna metadata vazio já que não temos acesso aos detalhes do Kafka
                    return new org.apache.kafka.clients.producer.RecordMetadata(
                            new org.apache.kafka.common.TopicPartition("orders.received", 0),
                            0, 0, System.currentTimeMillis(), 0, 0
                    );
                })
                .onFailure().invoke(throwable ->
                        LOGGER.errorf(throwable, "Erro ao publicar evento. orderId=%s", orderId)
                );
    }
}
