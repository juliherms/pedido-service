package org.acme.pedidos.dto.event;

public record EnderecoEntregaEvent (
        String rua,
        String cidade,
        String estado,
        String cep,
        String pais
) {}
