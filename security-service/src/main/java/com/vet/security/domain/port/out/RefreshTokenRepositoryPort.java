package com.vet.security.domain.port.out;

import com.vet.security.domain.model.RefreshToken;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    RefreshToken findByToken(String token);
}
