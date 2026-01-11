package org.acme.pedidos.producer;

import jakarta.enterprise.context.ApplicationScoped;
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
public class OrderEventProducer {

    private static final Logger LOGGER = Logger.getLogger(OrderEventProducer.class);
    private static final String EVENT_TYPE_HEADER = "eventType";
    private static final String SCHEMA_VERSION_HEADER = "schemaVersion";
    private static final String EVENT_TYPE_VALUE = "OrderReceived";
    private static final String SCHEMA_VERSION_VALUE = "1";

    @Inject
    @Channel("orders-out")
    Emitter<Record<String, PedidoRecebidoEvent>> emitter;

}
