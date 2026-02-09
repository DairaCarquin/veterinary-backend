package com.vet.gateway.domain.model;

import java.util.List;

public record TokenValidation(
        boolean valid,
        Long userId,
        List<String> roles
) {}