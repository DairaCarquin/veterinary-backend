package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.medical_history_service.domain.model.Treatment;

import reactor.core.publisher.Flux;

public interface R2dbcTreatmentRepository
        extends ReactiveCrudRepository<Treatment, Long> {

    Flux<Treatment> findByMedicalCaseId(Long medicalCaseId);

    Flux<Treatment> findByVeterinarianId(Long veterinarianId);

    Flux<Treatment> findByPetId(Long petId);
}