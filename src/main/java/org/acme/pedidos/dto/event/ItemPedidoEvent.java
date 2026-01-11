package org.acme.pedidos.dto.event;

import java.math.BigDecimal;

/**
 * Item do pedido no evento Kafka.
 */
public record ItemPedidoEvent(
        String idProduto,
        String nomeProdduto,
        Integer quantidade,
        BigDecimal valorUnitario
) {}
