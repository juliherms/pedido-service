package org.acme.pedidos.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.pedidos.dto.ErrorResponse;
import org.acme.pedidos.dto.ValidationError;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Exception mapper para tratar erros de validação Bean Validation.
 */
@Provider
public class ConstraintViolationExceptionMapper  implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LOG.warnf("Erro de validação: %s", exception.getMessage());

        List<ValidationError> violations = exception.getConstraintViolations().stream()
                .map(this::mapToValidationError)
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Bad Request",
                "Erro de validação nos dados fornecidos",
                violations
        );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

    private ValidationError mapToValidationError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        return new ValidationError(field, message);
    }
}
