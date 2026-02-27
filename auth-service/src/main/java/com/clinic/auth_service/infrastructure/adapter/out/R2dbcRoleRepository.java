package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.clinic.auth_service.domain.model.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcRoleRepository
        extends ReactiveCrudRepository<Role, Long> {

    Mono<Role> findByName(String name);

    Flux<Role> findByEnabledTrue();

    @Query("""
        SELECT * FROM roles
        WHERE enabled = true
        AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
        LIMIT :limit OFFSET :offset
    """)
    Flux<Role> search(String name, int limit, int offset);

    Mono<Long> count();

    Mono<Long> countByEnabledTrue();
}