package org.acme.pedidos.dto;

/**
 * Representa um erro de validação individual.
 */
public record ValidationError(
        String field,
        String message
) {}
