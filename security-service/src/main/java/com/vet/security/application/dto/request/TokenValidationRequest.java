package com.vet.security.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest(
        @NotBlank String token) {
}