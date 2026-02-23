package com.clinic.client_service.infrastructure.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.clinic.client_service.domain.event.UserCreatedEvent;
import com.clinic.client_service.domain.model.Client;
import com.clinic.client_service.domain.port.ClientRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final ClientRepositoryPort repository;

    @KafkaListener(topics = "user-created-topic", groupId = "client-group")
    public void consume(UserCreatedEvent event) {

        if (!event.getRole().equals("CLIENT")) {
            return;
        }

        Client client = Client.builder()
                .userId(event.getId())
                .username(event.getUsername())
                .build();

        repository.save(client).subscribe();
    }
}