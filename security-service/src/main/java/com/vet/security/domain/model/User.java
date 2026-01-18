package com.vet.security.domain.model;

import java.time.Instant;
import java.util.Set;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private boolean enabled;
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;

}