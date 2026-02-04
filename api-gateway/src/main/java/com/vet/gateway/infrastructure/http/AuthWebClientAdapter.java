package com.vet.gateway.infrastructure.http;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.vet.gateway.domain.model.TokenValidation;
import com.vet.gateway.domain.port.out.AuthServicePort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthWebClientAdapter implements AuthServicePort {

    private final WebClient webClient;

    @Override
    public TokenValidation validateToken(String token) {

        return webClient.post()
                .uri("/auth/validate")
                .bodyValue(java.util.Map.of("token", token))
                .retrieve()
                .bodyToMono(TokenValidation.class)
                .block();
    }
}
