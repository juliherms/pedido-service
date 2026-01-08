package org.acme.pedidos.dto.request;

public record EnderecoEntregaRequest(
        String rua,
        String cidade,
        String estado,
        String cep,
        String pais
) {
}
