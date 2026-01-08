package org.acme.pedidos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriarPedidoRequest(
        @NotBlank(message = "clienteId é obrigatório")
        String clienteId,

        @NotEmpty(message = "itens deve conter pelo menos um item")
        @Valid
        List<ItemPedidoRequest>itens,

        @NotNull(message = "endereco de entrega é obrigatório")
        @Valid
        EnderecoEntregaRequest enderecoEntregaRequest
) {


}
