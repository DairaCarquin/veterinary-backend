package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.auth_service.domain.model.User;
import com.clinic.auth_service.domain.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
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

    @Override
    public Mono<User> findByDni(String dni) {
        return repository.findByDni(dni);
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    @Override
    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<User> search(String username, Long roleId, int limit, int offset) {
        return repository.search(username, roleId, limit, offset);
    }

    @Override
    public Mono<Long> countAll() {
        return repository.count();
    }

    @Override
    public Mono<Long> countEnabled() {
        return repository.countByEnabledTrue();
    }

    @Override
    public Mono<Long> countFiltered(String username, Long roleId) {
        return repository.countFiltered(username, roleId);
    }
}
