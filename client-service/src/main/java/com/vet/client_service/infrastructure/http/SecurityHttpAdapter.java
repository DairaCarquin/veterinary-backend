package com.vet.client_service.infrastructure.http;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.vet.client_service.domain.port.out.SecurityClientPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityHttpAdapter implements SecurityClientPort {

    private final WebClient webClient;

    @Override
    public Long registerClientUser(String email, String dni) {

        Map<String, Object> body = Map.of(
                "username", email,
                "password", dni,
                "roles", List.of("CLIENTE")
        );

        return webClient.post()
                .uri("/auth/register")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }
}
