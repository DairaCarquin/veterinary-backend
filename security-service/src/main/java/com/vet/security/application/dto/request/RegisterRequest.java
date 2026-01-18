package com.vet.security.application.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,

        @NotBlank @Size(min = 8) String password,

        Set<String> roles) {
}