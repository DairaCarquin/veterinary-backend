package com.clinic.vet_service.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

        return repository.save(vet)
                .doOnSuccess(saved -> kafkaTemplate.send("veterinarian-available-topic",
                        new VeterinarianAvailableEvent(
                                saved.getId(),
                                saved.getName(),
                                saved.getSpecialty())));
    }

    public Flux<Veterinarian> findAll() {
        return repository.findAll();
    }

    public Flux<Veterinarian> findAvailable() {
        return repository.findByAvailable(true);
    }

    public Mono<Veterinarian> update(Long userId, UpdateVetRequest request) {

        return repository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Veterinario no encontrado")))
                .flatMap(vet -> {

                    vet.setName(request.getName());
                    vet.setSpecialty(request.getSpecialty());
                    vet.setLicenseNumber(request.getLicenseNumber());
                    vet.setEmail(request.getEmail());
                    vet.setAvailable(true);

                    return repository.save(vet);
                });
    }
}