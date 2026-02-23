package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.auth_service.domain.model.User;
import com.clinic.auth_service.domain.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcUserRepositoryAdapter implements UserRepositoryPort {

    private final R2dbcUserRepository repository;

    @Override
    public Mono<User> save(User user) {
        return repository.save(user);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}