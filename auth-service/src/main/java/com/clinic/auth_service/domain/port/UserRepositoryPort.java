package com.clinic.auth_service.domain.port;

import com.clinic.auth_service.domain.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {

    Mono<User> save(User user);

    Mono<User> findByUsername(String username);

    Mono<User> findByDni(String dni);

    Mono<User> findByPhone(String phone);

    Mono<User> findById(Long id);

    Flux<User> search(String username, Long roleId, int limit, int offset);

    Mono<Long> countAll();

    Mono<Long> countEnabled();

    Mono<Long> countFiltered(String username, Long roleId);
}
