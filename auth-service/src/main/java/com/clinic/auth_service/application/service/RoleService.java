package com.clinic.auth_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.auth_service.domain.model.Role;
import com.clinic.auth_service.infrastructure.adapter.out.R2dbcRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final R2dbcRoleRepository repository;

    public Mono<Role> create(Role role) {
        String normalizedName = role.getName().trim().toUpperCase(Locale.ROOT);

        return repository.findByName(normalizedName)
                .flatMap(existing -> Mono.<Role>error(new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El rol ya existe")))
                .switchIfEmpty(Mono.defer(() -> {
                    role.setName(normalizedName);
                    role.setEnabled(true);
                    role.setCreatedAt(LocalDateTime.now());
                    return repository.save(role);
                }));
    }

    public Mono<Map<String, Object>> list(Long id, String name, int page, int size) {
        int offset = page * size;

        return repository.search(id, name, size, offset)
                .collectList()
                .zipWith(repository.countFiltered(id, name))
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    public Mono<Role> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado")));
    }

    public Mono<Role> update(Long id, Role updated) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado")))
                .flatMap(role -> {
                    role.setName(updated.getName().trim().toUpperCase(Locale.ROOT));
                    return repository.save(role);
                });
    }

    public Mono<Void> toggle(Long id, boolean enabled) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado")))
                .flatMap(role -> {
                    role.setEnabled(enabled);
                    return repository.save(role);
                })
                .then();
    }
}
