package com.clinic.medical_history_service.infrastructure.config;

import com.clinic.medical_history_service.infrastructure.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Error interno del servidor";

        if (ex instanceof BusinessException be) {
            status = be.getStatus();
            message = be.getMessage();
        }

        String body = """
                {
                  "timestamp": "%s",
                  "status": %d,
                  "error": "%s",
                  "message": "%s",
                  "path": "%s"
                }
                """.formatted(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                exchange.getRequest().getPath());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        var buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}