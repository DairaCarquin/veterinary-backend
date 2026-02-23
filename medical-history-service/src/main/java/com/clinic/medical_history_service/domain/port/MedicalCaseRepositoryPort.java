package com.clinic.medical_history_service.domain.port;

import com.clinic.medical_history_service.domain.model.MedicalCase;
import reactor.core.publisher.Mono;

public interface MedicalCaseRepositoryPort {

    Mono<MedicalCase> save(MedicalCase medicalCase);

    Mono<MedicalCase> findById(Long id);

    Mono<MedicalCase> findByAppointmentId(Long appointmentId);
}