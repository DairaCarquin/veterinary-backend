package com.vet.pet.infrastructure.kafka;

import com.vet.pet.domain.model.Pet;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String PET_REGISTERED_TOPIC = "PET_REGISTERED";

    @Async
    public void publishPetRegistered(Pet pet) {
        try {
            String message = objectMapper.writeValueAsString(pet);
            kafkaTemplate.send(PET_REGISTERED_TOPIC, pet.getId().toString(), message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Pet registered event published successfully: {}", pet.getId());
                        } else {
                            log.warn("Failed to publish pet registered event for pet {}: {}",
                                    pet.getId(), ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.warn("Error preparing pet registered event for pet {}: {}", pet.getId(), e.getMessage());
        }
    }

}
