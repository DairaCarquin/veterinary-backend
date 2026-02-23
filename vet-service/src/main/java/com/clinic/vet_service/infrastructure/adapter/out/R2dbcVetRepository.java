package com.clinic.vet_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.vet_service.domain.model.Veterinarian;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcVetRepository extends ReactiveCrudRepository<Veterinarian, Long> {

    Flux<Veterinarian> findByAvailable(Boolean available);

    Mono<Veterinarian> findByUserId(Long userId);
}