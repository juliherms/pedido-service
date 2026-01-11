package org.acme.pedidos.exceptions;

/**
 * Exception lançada quando há falha na publicação de evento Kafka.
 */
public class KafkaPublishException extends RuntimeException {

    public KafkaPublishException(String message) {
        super(message);
    }

    public KafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
