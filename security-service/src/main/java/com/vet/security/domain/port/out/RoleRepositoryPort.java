package com.vet.security.domain.port.out;

import com.vet.security.domain.exception.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepositoryPort {
    Role findByName(String name);
    Set<Role> findByNames(Set<String> names);
    Optional<Role> findById(Long id);
    List<Role> findAll();
    Role save(Role role);
    void deleteById(Long id);
    boolean existsByName(String name);
    boolean isRoleInUse(Long roleId);
}
