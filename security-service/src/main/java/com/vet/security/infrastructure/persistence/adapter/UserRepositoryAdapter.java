package com.vet.security.infrastructure.persistence.adapter;

import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.exception.model.User;
import com.vet.security.domain.port.out.UserRepositoryPort;
import com.vet.security.infrastructure.persistence.entity.RoleEntity;
import com.vet.security.infrastructure.persistence.entity.UserEntity;
import com.vet.security.infrastructure.persistence.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override
    public Boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Page<User> findAllFiltered(String username, Boolean enabled, String role, Pageable pageable) {
        // Implementación simplificada - puede mejorarse con Specifications
        return repository.findAll(pageable).map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .enabled(entity.isEnabled())
                .status(entity.getStatus()) // ← NUEVO campo status
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .roles(entity.getRoles().stream()
                        .map(roleEntity -> Role.builder()
                                .id(roleEntity.getId())
                                .name(roleEntity.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEnabled(user.isEnabled());
        entity.setStatus(user.getStatus()); // ← NUEVO campo status
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        if (user.getRoles() != null) {
            entity.setRoles(user.getRoles().stream()
                    .map(role -> {
                        RoleEntity roleEntity = new RoleEntity();
                        roleEntity.setId(role.getId());
                        roleEntity.setName(role.getName());
                        return roleEntity;
                    })
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}
