package com.clinic.pet_service.infrastructure.adapter.in;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.pet_service.application.service.PetService;
import com.clinic.pet_service.domain.model.Pet;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    @PostMapping
    public Mono<Pet> create(@RequestBody Pet pet) {
        return service.create(pet);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY','CLIENT')")
    @GetMapping
    public Flux<Pet> findAll() {
        return service.findAll();
    }
}