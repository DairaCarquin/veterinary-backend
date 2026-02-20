package com.vet.security.infrastructure.kafka;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.exception.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(AuthEventProducer.class);
    private static final String TOPIC = "auth-user-events";
    private static final String EVENT_VERSION = "1.0";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(User user) {
        Map<String, Object> event = createBaseEvent("USER_CREATED", user);
        event.put("roles", user.getRoles().stream().map(Role::getName).toList());
        event.put("enabled", user.isEnabled());
        
        publishEvent(event);
        logger.info("USER_CREATED event published for user: {}", user.getUsername());
    }

    public void publishUserUpdated(User user) {
        Map<String, Object> event = createBaseEvent("USER_UPDATED", user);
        event.put("roles", user.getRoles().stream().map(Role::getName).toList());
        event.put("enabled", user.isEnabled());
        
        publishEvent(event);
        logger.info("USER_UPDATED event published for user: {}", user.getUsername());
    }

    public void publishRoleAssigned(User user) {
        Map<String, Object> event = createBaseEvent("ROLE_ASSIGNED", user);
        event.put("roles", user.getRoles().stream().map(Role::getName).toList());
        
        publishEvent(event);
        logger.info("ROLE_ASSIGNED event published for user: {}", user.getUsername());
    }

    public void publishUserDisabled(User user) {
        Map<String, Object> event = createBaseEvent("USER_DISABLED", user);
        
        publishEvent(event);
        logger.info("USER_DISABLED event published for user: {}", user.getUsername());
    }
    
    private Map<String, Object> createBaseEvent(String eventType, User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventVersion", EVENT_VERSION);
        event.put("eventType", eventType);
        event.put("userId", user.getId());
        event.put("username", user.getUsername());
        event.put("timestamp", Instant.now().toString());
        return event;
    }
    
    private void publishEvent(Map<String, Object> event) {
        try {
            kafkaTemplate.send(TOPIC, event);
        } catch (Exception e) {
            logger.error("Error publishing event to Kafka: {}", event, e);
        }
    }
}
