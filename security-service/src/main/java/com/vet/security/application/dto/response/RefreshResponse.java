package com.vet.security.application.dto.response;

public record RefreshResponse(
        String accessToken,
        long expiresIn) {
}