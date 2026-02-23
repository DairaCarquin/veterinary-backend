package com.clinic.pet_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.pet_service.domain.model.Pet;

import reactor.core.publisher.Flux;

public interface R2dbcPetRepository
        extends ReactiveCrudRepository<Pet, Long> {

    Flux<Pet> findByOwnerId(Long ownerId);
}