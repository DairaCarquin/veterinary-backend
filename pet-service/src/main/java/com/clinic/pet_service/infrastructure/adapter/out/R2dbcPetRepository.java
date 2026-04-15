package com.clinic.pet_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.pet_service.domain.model.Pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcPetRepository
        extends ReactiveCrudRepository<Pet, Long> {

    Flux<Pet> findByOwnerId(Long ownerId);

    Flux<Pet> findByOwnerIdAndEnabledTrue(Long ownerId);

        @Query("""
        SELECT * FROM pets
        WHERE enabled = true
        AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:species IS NULL OR LOWER(species) LIKE LOWER(CONCAT('%', :species, '%')))
        AND (:ownerId IS NULL OR owner_id = :ownerId)
        LIMIT :limit OFFSET :offset
    """)
    Flux<Pet> search(String name, String species, Long ownerId, int limit, int offset);

    @Query("""
        SELECT COUNT(*) FROM pets
        WHERE enabled = true
        AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:species IS NULL OR LOWER(species) LIKE LOWER(CONCAT('%', :species, '%')))
        AND (:ownerId IS NULL OR owner_id = :ownerId)
    """)
    Mono<Long> countFiltered(String name, String species, Long ownerId);

    Mono<Long> count();

    Mono<Long> countByEnabledTrue();
}
