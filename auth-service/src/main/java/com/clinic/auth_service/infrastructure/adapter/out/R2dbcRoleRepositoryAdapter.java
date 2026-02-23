package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.auth_service.domain.model.Role;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcRoleRepositoryAdapter {

    private final R2dbcRoleRepository repository;

    public Mono<Role> findByName(String name) {
        return repository.findByName(name);
    }

    public Mono<Role> findById(Long id) {
        return repository.findById(id);
    }
}