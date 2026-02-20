package com.vet.security.domain.port.out;

import com.vet.security.application.dto.response.TokenValidationResponse;
import com.vet.security.domain.exception.model.User;

public interface TokenProviderPort {
    String generateAccessToken(User user);
    boolean validateToken(String token);
    TokenValidationResponse parseToken(String token);
}
