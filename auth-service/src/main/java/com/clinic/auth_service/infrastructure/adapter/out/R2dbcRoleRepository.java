package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.auth_service.domain.model.Role;

import reactor.core.publisher.Mono;

public interface R2dbcRoleRepository
        extends ReactiveCrudRepository<Role, Long> {

    Mono<Role> findByName(String name);
}