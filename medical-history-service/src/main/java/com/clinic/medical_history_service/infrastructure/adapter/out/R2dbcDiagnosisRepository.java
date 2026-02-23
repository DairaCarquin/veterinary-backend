package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Diagnosis;
import reactor.core.publisher.Flux;

public interface R2dbcDiagnosisRepository extends ReactiveCrudRepository<Diagnosis, Long> {

    Flux<Diagnosis> findByMedicalCaseId(Long medicalCaseId);

    Flux<Diagnosis> findByVeterinarianId(Long veterinarianId);

    Flux<Diagnosis> findByPetId(Long petId);
}