package org.acme.pedidos.producer;

import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import org.acme.pedidos.dto.event.PedidoRecebidoEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

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
        return null;
    }

}
