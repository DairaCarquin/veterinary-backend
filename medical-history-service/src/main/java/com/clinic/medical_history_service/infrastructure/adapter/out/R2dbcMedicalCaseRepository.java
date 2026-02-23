package com.clinic.medical_history_service.infrastructure.adapter.out;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.medical_history_service.domain.model.MedicalCase;

import reactor.core.publisher.Mono;

public interface R2dbcMedicalCaseRepository
        extends ReactiveCrudRepository<MedicalCase, Long> {
        Mono<MedicalCase> findByAppointmentId(Long appointmentId);
}