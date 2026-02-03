package com.vet.gateway.domain.port.out;

import com.vet.gateway.domain.model.TokenValidation;

public interface AuthServicePort {
    TokenValidation validateToken(String token);
}