package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Diagnosis;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcDiagnosisRepository extends ReactiveCrudRepository<Diagnosis, Long> {

    Flux<Diagnosis> findByMedicalCaseId(Long medicalCaseId);

    Flux<Diagnosis> findByVeterinarianId(Long veterinarianId);

    Flux<Diagnosis> findByPetId(Long petId);

    @Query("""
            SELECT d.* FROM diagnosis d
            INNER JOIN medical_case mc ON mc.id = d.medical_case_id
            WHERE (:caseId IS NULL OR d.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR d.pet_id = :petId)
            AND (:vetId IS NULL OR d.veterinarian_id = :vetId)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Diagnosis> search(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId,
            int limit,
            int offset);

    @Query("""
            SELECT COUNT(*) FROM diagnosis d
            INNER JOIN medical_case mc ON mc.id = d.medical_case_id
            WHERE (:caseId IS NULL OR d.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR d.pet_id = :petId)
            AND (:vetId IS NULL OR d.veterinarian_id = :vetId)
            """)
    Mono<Long> countFiltered(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId);
}
