package com.clinic.pet_service.application.service;

import java.time.LocalDateTime;

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
                .doOnSuccess(saved -> kafkaTemplate.send("pet-registered-topic",
                        new PetRegisteredEvent(
                                saved.getId(),
                                saved.getName(),
                                saved.getOwnerId())));
    }

    public Flux<Pet> findAll(String name, String species, Long ownerId, int page, int size) {
        int offset = page * size;
        return repository.findAll(name, species, ownerId, size, offset);
    }

    public Mono<Pet> update(Long id, Pet updated) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(updated.getName());
                    existing.setSpecies(updated.getSpecies());
                    existing.setBreed(updated.getBreed());
                    existing.setAge(updated.getAge());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                });
    }

    public Mono<Void> toggleEnabled(Long id, boolean enabled) {
        return repository.findById(id)
                .flatMap(pet -> {
                    pet.setEnabled(enabled);
                    pet.setUpdatedAt(LocalDateTime.now());
                    return repository.save(pet);
                })
                .then();
    }

    public Mono<Double> getActivePercentage() {
        return Mono.zip(repository.countEnabled(), repository.countAll())
                .map(tuple -> {
                    long enabled = tuple.getT1();
                    long total = tuple.getT2();
                    if (total == 0)
                        return 0.0;
                    return (enabled * 100.0) / total;
                });
    }

    public Mono<Long> countAll() {
        return repository.countAll();
    }
}