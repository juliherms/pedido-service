package org.acme.pedidos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemPedidoRequest(
        @NotBlank(message = "idProduto é obrigatório")
        String idProduto,

        @NotBlank(message = "nomeProduto é obrigatório")
        String nomeProduto,

        @NotNull(message = "quantidade é obrigatória")
        @Min(value = 1, message = "quanitdade deve ser mairo ou igual a 1")
        Integer quantidade,

        @NotNull(message = "valorUnitário é obrigatório")
        @DecimalMin(value = "0.01", message = "valorUnitário deve ser maior que 0")
        BigDecimal valorUnitario
) {
}
