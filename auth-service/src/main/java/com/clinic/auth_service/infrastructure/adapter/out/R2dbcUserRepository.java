package com.clinic.auth_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.clinic.auth_service.domain.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcUserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByUsername(String username);

    Mono<User> findByDni(String dni);

    Mono<User> findByPhone(String phone);

    Mono<User> findByUsernameAndEnabledTrue(String username);

    @Query("""
                SELECT * FROM users
                WHERE enabled = true
                AND (:username IS NULL OR LOWER(username) LIKE LOWER(CONCAT('%', :username, '%')))
                AND (:roleId IS NULL OR role_id = :roleId)
                LIMIT :limit OFFSET :offset
            """)
    Flux<User> search(String username, Long roleId, int limit, int offset);

    @Query("""
                SELECT COUNT(*) FROM users
                WHERE enabled = true
                AND (:username IS NULL OR LOWER(username) LIKE LOWER(CONCAT('%', :username, '%')))
                AND (:roleId IS NULL OR role_id = :roleId)
            """)
    Mono<Long> countFiltered(String username, Long roleId);

    Mono<Long> count();

    Mono<Long> countByEnabledTrue();
}
