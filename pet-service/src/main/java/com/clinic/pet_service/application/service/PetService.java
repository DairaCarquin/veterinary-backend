package com.clinic.pet_service.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.clinic.pet_service.domain.event.PetRegisteredEvent;
import com.clinic.pet_service.domain.model.Pet;
import com.clinic.pet_service.domain.port.PetRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepositoryPort repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Pet> create(Pet pet) {

        return repository.save(pet)
                .doOnSuccess(saved ->
                        kafkaTemplate.send("pet-registered-topic",
                                new PetRegisteredEvent(
                                        saved.getId(),
                                        saved.getName(),
                                        saved.getOwnerId())));
    }

    public Flux<Pet> findAll() {
        return repository.findAll();
    }

    public Flux<Pet> findByOwner(Long ownerId) {
        return repository.findByOwnerId(ownerId);
    }
}