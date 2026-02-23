package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.clinic.auth_service.domain.model.User;

import reactor.core.publisher.Mono;

@Repository
public interface R2dbcUserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByUsername(String username);
}
