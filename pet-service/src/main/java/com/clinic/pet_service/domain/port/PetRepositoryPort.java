package com.clinic.pet_service.domain.port;

import com.clinic.pet_service.domain.model.Pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PetRepositoryPort {

    Mono<Pet> save(Pet pet);

    Mono<Pet> findById(Long id);

    Flux<Pet> findByOwnerId(Long ownerId);

    Flux<Pet> findAll();
}