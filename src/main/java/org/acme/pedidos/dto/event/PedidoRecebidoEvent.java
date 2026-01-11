package org.acme.pedidos.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Evento publicado no Kafka quando um pedido é recebido.
 */
public record  PedidoRecebidoEvent(
        String eventId,
        String pedidoId,
        String clienteId,
        List<ItemPedidoEvent> itens,
        EnderecoEntregaEvent enderecoEntrega,
        Integer totalItens,
        BigDecimal valorTotal,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant criadoEm
) {}
