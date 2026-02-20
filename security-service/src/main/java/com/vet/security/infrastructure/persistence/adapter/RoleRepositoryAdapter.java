package com.vet.security.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import com.vet.security.infrastructure.persistence.entity.RoleEntity;
import com.vet.security.infrastructure.persistence.repository.RoleJpaRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository repository;

    @Override
    public Role findByName(String name) {
        RoleEntity entity = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));
        return toDomain(entity);
    }

    @Override
    public Set<Role> findByNames(Set<String> names) {
        Set<RoleEntity> entities = repository.findByNameIn(names);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Role> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = toEntity(role);
        RoleEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public boolean isRoleInUse(Long roleId) {
        return repository.isRoleInUse(roleId);
    }

    private Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private RoleEntity toEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        return entity;
    }
}
