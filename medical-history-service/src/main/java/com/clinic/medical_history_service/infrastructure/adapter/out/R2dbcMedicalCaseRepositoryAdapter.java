package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.clinic.medical_history_service.domain.model.MedicalCase;
import com.clinic.medical_history_service.domain.port.MedicalCaseRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcMedicalCaseRepositoryAdapter implements MedicalCaseRepositoryPort {

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

    @Override
    public Flux<MedicalCase> search(
            Long appointmentId,
            Long clientId,
            Long petId,
            Long veterinarianId,
            int limit,
            int offset) {
        return repository.search(appointmentId, clientId, petId, veterinarianId, limit, offset);
    }

    @Override
    public Mono<Long> countFiltered(
            Long appointmentId,
            Long clientId,
            Long petId,
            Long veterinarianId) {
        return repository.countFiltered(appointmentId, clientId, petId, veterinarianId);
    }

    @Override
    public Mono<Long> findClientIdByUserId(Long userId) {
        return repository.findClientIdByUserId(userId);
    }
}
