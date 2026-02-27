package com.clinic.auth_service.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.auth_service.application.service.RoleService;
import com.clinic.auth_service.domain.model.Role;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    public Mono<Role> create(@RequestBody Role role) {
        return service.create(role);
    }

    @GetMapping
    public Mono<Map<String, Object>> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.list(name, page, size);
    }

    @PutMapping("/{id}")
    public Mono<Role> update(@PathVariable Long id,
            @RequestBody Role role) {
        return service.update(id, role);
    }

    @PatchMapping("/{id}/status")
    public Mono<Void> toggle(@PathVariable Long id,
            @RequestParam boolean enabled) {
        return service.toggle(id, enabled);
    }
}