package com.clinic.vet_service.domain.port;

import com.clinic.vet_service.domain.model.Veterinarian;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VetRepositoryPort {

    Mono<Veterinarian> save(Veterinarian vet);

    Mono<Veterinarian> findById(Long id);

    Flux<Veterinarian> findAll();

    Flux<Veterinarian> findByAvailable(Boolean available);

    Mono<Veterinarian> findByUserId(Long userId);
}