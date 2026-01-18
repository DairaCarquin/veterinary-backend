package com.vet.security.application.dto.response;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        List<String> roles) {
}