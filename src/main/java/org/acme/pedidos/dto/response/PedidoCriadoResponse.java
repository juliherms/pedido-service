package org.acme.pedidos.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoCriadoResponse(
        String idPedido,
        String idCliente,
        Integer totalItens,
        BigDecimal valorTotal,
        String status,
        Instant criandoEm
) {
}
