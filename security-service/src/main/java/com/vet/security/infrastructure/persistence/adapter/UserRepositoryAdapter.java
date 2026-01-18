package com.vet.security.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.vet.security.domain.model.User;
import com.vet.security.domain.port.out.UserRepositoryPort;
import com.vet.security.infrastructure.persistence.entity.UserEntity;
import com.vet.security.infrastructure.persistence.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .map(this::toDomain)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setEnabled(entity.isEnabled());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());

        user.setRoles(
                entity.getRoles().stream()
                        .map(roleEntity -> {
                            var role = new com.vet.security.domain.model.Role();
                            role.setId(roleEntity.getId());
                            role.setName(roleEntity.getName());
                            return role;
                        })
                        .collect(java.util.stream.Collectors.toSet()));

        return user;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEnabled(user.isEnabled());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        entity.setRoles(
                user.getRoles().stream()
                        .map(role -> {
                            var roleEntity = new com.vet.security.infrastructure.persistence.entity.RoleEntity();
                            roleEntity.setId(role.getId());
                            roleEntity.setName(role.getName());
                            return roleEntity;
                        })
                        .collect(java.util.stream.Collectors.toSet()));

        return entity;
    }
}
