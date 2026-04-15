package com.clinic.pet_service.application.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
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

    public Mono<Pet> create(Pet pet, String role, Long authenticatedUserId) {
        if ("CLIENT".equals(role) && !authenticatedUserId.equals(pet.getOwnerId())) {
            return Mono.error(new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puede registrar mascotas para otro cliente"));
        }

        pet.setEnabled(true);
        pet.setCreatedAt(LocalDateTime.now());

        return repository.save(pet)
                .doOnSuccess(saved -> kafkaTemplate.send("pet-registered-topic",
                        new PetRegisteredEvent(
                                saved.getId(),
                                saved.getName(),
                                saved.getOwnerId())));
    }

    public Mono<Pet> findById(Long id, String role, Long authenticatedUserId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mascota no encontrada")))
                .flatMap(pet -> {
                    if ("CLIENT".equals(role) && !authenticatedUserId.equals(pet.getOwnerId())) {
                        return Mono.error(new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "No puede consultar mascotas de otro cliente"));
                    }
                    return Mono.just(pet);
                });
    }

    public Flux<Pet> findAll(String name, String species, Long ownerId, String role, Long authenticatedUserId, int page,
            int size) {
        int offset = page * size;
        Long resolvedOwnerId = "CLIENT".equals(role) ? authenticatedUserId : ownerId;
        return repository.findAll(name, species, resolvedOwnerId, size, offset);
    }

    public Mono<Long> countFiltered(String name, String species, Long ownerId, String role, Long authenticatedUserId) {
        Long resolvedOwnerId = "CLIENT".equals(role) ? authenticatedUserId : ownerId;
        return repository.countFiltered(name, species, resolvedOwnerId);
    }

    public Mono<Pet> update(Long id, Pet updated, String role, Long authenticatedUserId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mascota no encontrada")))
                .flatMap(existing -> {
                    if ("CLIENT".equals(role) && !authenticatedUserId.equals(existing.getOwnerId())) {
                        return Mono.error(new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "No puede actualizar mascotas de otro cliente"));
                    }

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
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mascota no encontrada")))
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
                    if (total == 0) {
                        return 0.0;
                    }
                    return (enabled * 100.0) / total;
                });
    }

    public Mono<Long> countAll() {
        return repository.countAll();
    }
}
