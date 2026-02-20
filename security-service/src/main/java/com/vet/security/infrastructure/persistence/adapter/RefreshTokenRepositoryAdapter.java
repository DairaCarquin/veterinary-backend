package com.vet.security.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.vet.security.domain.exception.model.RefreshToken;
import com.vet.security.domain.port.out.RefreshTokenRepositoryPort;
import com.vet.security.infrastructure.persistence.entity.RefreshTokenEntity;
import com.vet.security.infrastructure.persistence.repository.RefreshTokenJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository repository;

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = toEntity(token);
        RefreshTokenEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public RefreshToken findByToken(String token) {
        RefreshTokenEntity entity = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));
        return toDomain(entity);
    }

    @Override
    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        RefreshToken token = new RefreshToken();
        token.setId(entity.getId());
        token.setToken(entity.getToken());
        token.setUserId(entity.getUserId());
        token.setExpiresAt(entity.getExpiresAt());
        token.setRevoked(entity.isRevoked());
        return token;
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(token.getId());
        token.setToken(token.getToken());
        entity.setUserId(token.getUserId());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setRevoked(token.isRevoked());
        return entity;
    }
}
