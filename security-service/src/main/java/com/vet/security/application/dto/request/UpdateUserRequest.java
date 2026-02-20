package com.vet.security.application.dto.request;

import java.util.Set;

public record UpdateUserRequest(
        Set<String> roles,
        Boolean enabled
) {
}
