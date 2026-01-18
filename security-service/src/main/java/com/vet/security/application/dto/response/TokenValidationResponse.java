package com.vet.security.application.dto.response;

import java.util.List;

public record TokenValidationResponse(
        boolean valid,
        Long userId,
        List<String> roles) {
}
