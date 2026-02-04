package com.vet.client_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient securityWebClient() {
        return WebClient.builder()
                .baseUrl("http://security-service:8803")
                .build();
    }
}