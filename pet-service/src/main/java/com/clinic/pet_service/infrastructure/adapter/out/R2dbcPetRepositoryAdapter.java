package com.clinic.pet_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.pet_service.domain.model.Pet;
import com.clinic.pet_service.domain.port.PetRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcPetRepositoryAdapter implements PetRepositoryPort {

    private final R2dbcPetRepository repository;

    @Override
    public Mono<Pet> save(Pet pet) {
        return repository.save(pet);
    }

    @Override
    public Mono<Pet> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Pet> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId);
    }

    @Override
    public Flux<Pet> findAll() {
        return repository.findAll();
    }
}