package com.clinic.pet_service.infrastructure.adapter.in;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.pet_service.application.service.PetService;
import com.clinic.pet_service.domain.model.Pet;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Pet> create(@RequestBody Pet pet) {
        return service.create(pet);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY','CLIENT')")
    public Mono<Map<String, Object>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.findAll(name, species, ownerId, page, size)
                .collectList()
                .zipWith(service.countAll())
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    public Mono<Pet> update(@PathVariable Long id, @RequestBody Pet pet) {
        return service.update(id, pet);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> toggle(@PathVariable Long id,
            @RequestParam boolean enabled) {
        return service.toggleEnabled(id, enabled);
    }

    @GetMapping("/stats/active-percentage")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Double> activePercentage() {
        return service.getActivePercentage();
    }
}