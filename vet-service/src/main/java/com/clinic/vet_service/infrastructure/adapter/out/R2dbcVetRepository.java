package com.clinic.vet_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.vet_service.domain.model.Veterinarian;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcVetRepository extends ReactiveCrudRepository<Veterinarian, Long> {

    Flux<Veterinarian> findByAvailable(Boolean available);

    Mono<Veterinarian> findByUserId(Long userId);

    @Query("""
                SELECT * FROM veterinarians
                WHERE enabled = true
                AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:specialty IS NULL OR LOWER(specialty) LIKE LOWER(CONCAT('%', :specialty, '%')))
                AND (:available IS NULL OR available = :available)
                LIMIT :limit OFFSET :offset
            """)
    Flux<Veterinarian> search(String name,
            String specialty,
            Boolean available,
            int limit,
            int offset);

    @Query("""
                SELECT COUNT(*) FROM veterinarians
                WHERE enabled = true
            """)
    Mono<Long> countEnabled();

    Mono<Long> count();
}