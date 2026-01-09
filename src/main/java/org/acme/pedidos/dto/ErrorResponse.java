package org.acme.pedidos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Payload padronizado de erro retornado pela API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<ValidationError> violations,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null, Instant.now());
    }

    public ErrorResponse(int status, String error, String message, List<ValidationError> violations) {
        this(status, error, message, violations, Instant.now());
    }
}
