package com.clinic.pet_service.domain.port;

import com.clinic.pet_service.domain.model.Pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PetRepositoryPort {

    Mono<Pet> save(Pet pet);

    Mono<Pet> findById(Long id);

    Flux<Pet> findByOwnerId(Long ownerId);

    Flux<Pet> findEnabledByOwnerId(Long ownerId);

    Flux<Pet> findAll(String name, String species, Long ownerId, int limit, int offset);

    Mono<Long> countFiltered(String name, String species, Long ownerId);

    Mono<Long> countAll();

    Mono<Long> countEnabled();

}
