package com.vet.security.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.vet.security.domain.model.Role;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import com.vet.security.infrastructure.persistence.entity.RoleEntity;
import com.vet.security.infrastructure.persistence.repository.RoleJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository repository;

    @Override
    public Role findByName(String name) {

        RoleEntity entity = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));

        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());

        return role;
    }
}