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
        if ("CLIENT".equals(role)) {
            return resolveClientId(authenticatedUserId)
                    .flatMap(clientId -> {
                        if (pet.getOwnerId() != null && !clientId.equals(pet.getOwnerId())) {
                            return Mono.error(new org.springframework.web.server.ResponseStatusException(
                                    HttpStatus.FORBIDDEN,
                                    "No puede registrar mascotas para otro cliente"));
                        }

                        pet.setOwnerId(clientId);
                        return saveNewPet(pet);
                    });
        }

        return saveNewPet(pet);
    }

    private Mono<Pet> saveNewPet(Pet pet) {
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
                .flatMap(pet -> validateClientOwnership(pet, role, authenticatedUserId).thenReturn(pet));
    }

    private Mono<Void> validateClientOwnership(Pet pet, String role, Long authenticatedUserId) {
        if (!"CLIENT".equals(role)) {
            return Mono.empty();
        }

        return resolveClientId(authenticatedUserId)
                .flatMap(clientId -> {
                    if (!clientId.equals(pet.getOwnerId())) {
                        return Mono.error(new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "No puede consultar mascotas de otro cliente"));
                    }

                    return Mono.empty();
                });
    }

    public Flux<Pet> findAll(String name, String species, Long ownerId, String role, Long authenticatedUserId, int page,
            int size) {
        int offset = page * size;
        if ("CLIENT".equals(role)) {
            return resolveClientId(authenticatedUserId)
                    .flatMapMany(clientId -> repository.findAll(name, species, clientId, size, offset));
        }

        Long resolvedOwnerId = ownerId;
        return repository.findAll(name, species, resolvedOwnerId, size, offset);
    }

    public Mono<Long> countFiltered(String name, String species, Long ownerId, String role, Long authenticatedUserId) {
        if ("CLIENT".equals(role)) {
            return resolveClientId(authenticatedUserId)
                    .flatMap(clientId -> repository.countFiltered(name, species, clientId));
        }

        return repository.countFiltered(name, species, ownerId);
    }

    public Mono<Pet> update(Long id, Pet updated, String role, Long authenticatedUserId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mascota no encontrada")))
                .flatMap(existing -> {
                    return validateClientOwnership(existing, role, authenticatedUserId)
                            .then(Mono.defer(() -> {
                                existing.setName(updated.getName());
                                existing.setSpecies(updated.getSpecies());
                                existing.setBreed(updated.getBreed());
                                existing.setAge(updated.getAge());
                                existing.setUpdatedAt(LocalDateTime.now());
                                return repository.save(existing);
                            }));
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

    private Mono<Long> resolveClientId(Long userId) {
        return repository.findClientIdByUserId(userId)
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No existe un cliente asociado al usuario autenticado")));
    }
}
