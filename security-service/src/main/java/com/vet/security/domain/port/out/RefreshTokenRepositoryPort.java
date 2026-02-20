package com.vet.security.domain.port.out;

import com.vet.security.domain.exception.model.RefreshToken;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    RefreshToken findByToken(String token);
    void deleteByUserId(Long userId);
}
