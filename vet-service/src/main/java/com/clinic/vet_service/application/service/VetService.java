package com.clinic.vet_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.vet_service.application.dto.request.UpdateVetRequest;
import com.clinic.vet_service.domain.event.VeterinarianAvailableEvent;
import com.clinic.vet_service.domain.model.Veterinarian;
import com.clinic.vet_service.domain.port.VetRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VetService {

    private final VetRepositoryPort repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Veterinarian> create(Veterinarian vet) {
        vet.setAvailable(true);
        vet.setEnabled(true);
        vet.setCreatedAt(LocalDateTime.now());

        return repository.save(vet)
                .doOnSuccess(saved -> kafkaTemplate.send("veterinarian-available-topic",
                        new VeterinarianAvailableEvent(
                                saved.getId(),
                                saved.getName(),
                                saved.getSpecialty())));
    }

    public Mono<Map<String, Object>> findAll(String name,
            String specialty,
            Boolean available,
            int page,
            int size) {

        int offset = page * size;

        return repository.search(name, specialty, available, size, offset)
                .collectList()
                .zipWith(repository.countFiltered(name, specialty, available))
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    public Flux<Veterinarian> findAvailable() {
        return repository.findByAvailable(true);
    }

    public Mono<Veterinarian> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Veterinario no encontrado")));
    }

    public Mono<Veterinarian> update(Long pathUserId, UpdateVetRequest request, String role, Long authenticatedUserId) {
        Long targetUserId = "VETERINARY".equals(role) ? authenticatedUserId : pathUserId;

        return repository.findByUserId(targetUserId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Veterinario no encontrado")))
                .flatMap(vet -> {
                    vet.setName(request.getName());
                    vet.setLastName(request.getLastName());
                    vet.setDni(request.getDni());
                    vet.setSpecialty(request.getSpecialty());
                    vet.setLicenseNumber(request.getLicenseNumber());
                    vet.setEmail(request.getEmail());
                    vet.setPhone(request.getPhone());
                    vet.setAvailable(true);
                    vet.setUpdatedAt(LocalDateTime.now());

                    return repository.save(vet);
                });
    }

    public Mono<Void> toggleEnabled(Long id, boolean enabled) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Veterinario no encontrado")))
                .flatMap(vet -> {
                    vet.setEnabled(enabled);
                    vet.setUpdatedAt(LocalDateTime.now());
                    return repository.save(vet);
                })
                .then();
    }
}
