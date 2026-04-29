package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Analysis;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcAnalysisRepository extends ReactiveCrudRepository<Analysis, Long> {

    Flux<Analysis> findByMedicalCaseId(Long medicalCaseId);

    Flux<Analysis> findByVeterinarianId(Long veterinarianId);

    Flux<Analysis> findByPetId(Long petId);

    @Query("""
            SELECT a.* FROM analysis a
            INNER JOIN medical_case mc ON mc.id = a.medical_case_id
            WHERE (:caseId IS NULL OR a.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR a.pet_id = :petId)
            AND (:vetId IS NULL OR a.veterinarian_id = :vetId)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Analysis> search(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId,
            int limit,
            int offset);

    @Query("""
            SELECT COUNT(*) FROM analysis a
            INNER JOIN medical_case mc ON mc.id = a.medical_case_id
            WHERE (:caseId IS NULL OR a.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR a.pet_id = :petId)
            AND (:vetId IS NULL OR a.veterinarian_id = :vetId)
            """)
    Mono<Long> countFiltered(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId);
}
