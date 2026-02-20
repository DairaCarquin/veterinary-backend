package com.vet.security.application.dto.response;

import com.vet.security.domain.exception.model.Role;

public record RoleResponse(
        Long id,
        String name
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName()
        );
    }
}
