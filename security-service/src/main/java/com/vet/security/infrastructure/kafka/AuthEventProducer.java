package com.vet.security.infrastructure.kafka;

import java.time.Instant;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.vet.security.domain.model.Role;
import com.vet.security.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(User user) {

        Map<String, Object> event = Map.of(
                "event", "USER_CREATED",
                "userId", user.getId(),
                "username", user.getUsername(),
                "roles", user.getRoles().stream().map(Role::getName).toList(),
                "timestamp", Instant.now().toString());

        kafkaTemplate.send("auth-user-events", event);
    }
}
