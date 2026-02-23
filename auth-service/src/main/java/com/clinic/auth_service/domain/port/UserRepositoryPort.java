package com.clinic.auth_service.domain.port;

import com.clinic.auth_service.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserRepositoryPort {

    Mono<User> save(User user);

    Mono<User> findByUsername(String username);
}