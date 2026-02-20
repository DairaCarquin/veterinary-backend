package com.vet.security.domain.exception.model;

import java.time.Instant;

import lombok.Data;

@Data
public class RefreshToken {

    private Long id;
    private String token;
    private Long userId;
    private Instant expiresAt;
    private boolean revoked;
}