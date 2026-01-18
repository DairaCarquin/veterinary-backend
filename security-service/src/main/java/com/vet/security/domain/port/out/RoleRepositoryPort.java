package com.vet.security.domain.port.out;

import com.vet.security.domain.model.Role;

public interface RoleRepositoryPort {
    Role findByName(String name);
}
