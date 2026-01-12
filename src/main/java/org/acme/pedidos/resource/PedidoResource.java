package org.acme.pedidos.resource;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.pedidos.dto.request.CriarPedidoRequest;
import org.acme.pedidos.service.PedidoService;
import org.jboss.logging.Logger;


@Path("/api/v1/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    private static final Logger LOOGER = Logger.getLogger(PedidoResource.class);

    @Inject
    PedidoService pedidoService;

    @Inject
    MeterRegistry meterRegistry;

    private Counter contadorRequisicoes;

    @PostConstruct
    void init() {
        contadorRequisicoes = meterRegistry.counter("http.requests.total",
                "method", "POST",
                "endpoint", "/api/v1/pedidos");
    }

    /**
     * Cria um novo pedido.
     *
     * @param request Dados do pedido
     * @return Response 201 Created com OrderCreatedResponse
     */
    @POST
    public Uni<Response> createOrder(@Valid CriarPedidoRequest request) {
        contadorRequisicoes.increment();

        LOOGER.infof("Recebida requisição para criar pedido. customerId=%s, itens=%d",
                request.clienteId(), request.itens().size());

        return pedidoService.createOrder(request)
                .map(response -> Response
                        .status(Response.Status.CREATED)
                        .entity(response)
                        .build()
                );
    }
}
