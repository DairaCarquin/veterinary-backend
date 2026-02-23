package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Analysis;
import reactor.core.publisher.Flux;

public interface R2dbcAnalysisRepository extends ReactiveCrudRepository<Analysis, Long> {

    Flux<Analysis> findByMedicalCaseId(Long medicalCaseId);

    Flux<Analysis> findByVeterinarianId(Long veterinarianId);

    Flux<Analysis> findByPetId(Long petId);
}