package com.vet.security.domain.exception.model;

import java.time.Instant;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private boolean enabled;
    private int status; // 1 = activo, 0 = inactivo (borrado lógico)
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return status == 1 && enabled;
    }
}
