package com.clinic.vet_service.infrastructure.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.clinic.vet_service.domain.event.VeterinaryCreatedEvent;
import com.clinic.vet_service.domain.model.Veterinarian;
import com.clinic.vet_service.domain.port.VetRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VeterinaryCreatedListener {

    private final VetRepositoryPort repository;

    @KafkaListener(
        topics = "user-created-topic", 
        groupId = "vet-group", 
        containerFactory = "kafkaListenerContainerFactory")
    public void handleUserCreated(VeterinaryCreatedEvent event) {

        if (event.getRole().equals("VETERINARY")) {

            Veterinarian vet = Veterinarian.builder()
                    .userId(event.getId())
                    .username(event.getUsername())
                    .available(false)
                    .build();

            repository.save(vet).subscribe();
        }
    }
}