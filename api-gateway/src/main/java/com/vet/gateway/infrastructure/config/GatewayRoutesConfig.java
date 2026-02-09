package com.vet.gateway.infrastructure.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
            .route("client-service", r -> r
                .path("/client-service/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .circuitBreaker(cb -> cb
                        .setName("clientServiceCB")
                        .setFallbackUri("forward:/fallback")))
                .uri("http://client-service:8805"))
            .build();
    }
}