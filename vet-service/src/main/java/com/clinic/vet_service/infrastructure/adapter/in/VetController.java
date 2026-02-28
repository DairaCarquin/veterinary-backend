package com.clinic.vet_service.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.vet_service.application.dto.request.UpdateVetRequest;
import com.clinic.vet_service.application.service.VetService;
import com.clinic.vet_service.domain.model.Veterinarian;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/vets")
@RequiredArgsConstructor
public class VetController {

    private final VetService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    public Mono<Map<String, Object>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.findAll(name, specialty, available, page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @GetMapping("/available")
    public Flux<Veterinarian> findAvailable() {
        return service.findAvailable();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public Mono<Veterinarian> update(
            @PathVariable Long userId,
            @RequestBody UpdateVetRequest request) {

        return service.update(userId, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> toggle(@PathVariable Long id,
            @RequestParam boolean enabled) {
        return service.toggleEnabled(id, enabled);
    }
}