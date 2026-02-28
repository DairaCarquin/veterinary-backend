package com.clinic.vet_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.vet_service.domain.model.Veterinarian;
import com.clinic.vet_service.domain.port.VetRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcVetRepositoryAdapter implements VetRepositoryPort {

    private final R2dbcVetRepository repository;

    @Override
    public Mono<Veterinarian> save(Veterinarian vet) {
        return repository.save(vet);
    }

    @Override
    public Mono<Veterinarian> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Veterinarian> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<Veterinarian> findByAvailable(Boolean available) {
        return repository.findByAvailable(available);
    }

    @Override
    public Mono<Veterinarian> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Flux<Veterinarian> search(String name,
            String specialty,
            Boolean available,
            int limit,
            int offset) {
        return repository.search(name, specialty, available, limit, offset);
    }

    @Override
    public Mono<Long> countEnabled() {
        return repository.countEnabled();
    }

    @Override
    public Mono<Long> countAll() {
        return repository.count();
    }
}