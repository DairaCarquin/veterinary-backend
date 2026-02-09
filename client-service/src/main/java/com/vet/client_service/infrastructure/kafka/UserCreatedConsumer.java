package com.vet.client_service.infrastructure.kafka;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

    @KafkaListener(
        topics = "auth-user-events",
        groupId = "client-service-group"
    )
    public void consume(Map<String, Object> event) {

        if (!"USER_CREATED".equals(event.get("event"))) {
            return;
        }

        System.out.println("📥 USER_CREATED recibido: " + event);
    }
}
