package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.medical_history_service.domain.model.MedicalCase;
import com.clinic.medical_history_service.domain.port.MedicalCaseRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcMedicalCaseRepositoryAdapter
        implements MedicalCaseRepositoryPort {

    private final R2dbcMedicalCaseRepository repository;

    @Override
    public Mono<MedicalCase> save(MedicalCase medicalCase) {
        return repository.save(medicalCase);
    }

    @Override
    public Mono<MedicalCase> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<MedicalCase> findByAppointmentId(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId);
    }
}