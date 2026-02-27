package com.clinic.auth_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clinic.auth_service.domain.model.Role;
import com.clinic.auth_service.infrastructure.adapter.out.R2dbcRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final R2dbcRoleRepository repository;

    public Mono<Role> create(Role role) {
        role.setEnabled(true);
        role.setCreatedAt(LocalDateTime.now());
        return repository.save(role);
    }

    public Mono<Map<String, Object>> list(String name, int page, int size) {

        int offset = page * size;

        return repository.search(name, size, offset)
                .collectList()
                .zipWith(repository.countByEnabledTrue())
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    public Mono<Role> update(Long id, Role updated) {
        return repository.findById(id)
                .flatMap(role -> {
                    role.setName(updated.getName());
                    return repository.save(role);
                });
    }

    public Mono<Void> toggle(Long id, boolean enabled) {
        return repository.findById(id)
                .flatMap(role -> {
                    role.setEnabled(enabled);
                    return repository.save(role);
                })
                .then();
    }
}