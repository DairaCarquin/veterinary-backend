package com.vet.security.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vet.security.infrastructure.persistence.entity.RefreshTokenEntity;

public interface RefreshTokenJpaRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);
}
