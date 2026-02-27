package com.clinic.client_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.clinic.client_service.domain.model.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcClientRepository extends ReactiveCrudRepository<Client, Long> {

    Mono<Long> countByEnabledTrue();

    Flux<Client> findByEnabledTrue();

    @Query("""
        SELECT * FROM clients 
        WHERE enabled = true
        AND (:name IS NULL OR LOWER(first_name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:dni IS NULL OR dni LIKE CONCAT('%', :dni, '%'))
        LIMIT :limit OFFSET :offset
    """)
    Flux<Client> findAll(String name, String dni, int limit, int offset);
}