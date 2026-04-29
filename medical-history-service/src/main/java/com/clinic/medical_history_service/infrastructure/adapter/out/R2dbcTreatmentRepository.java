package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.medical_history_service.domain.model.Treatment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcTreatmentRepository
        extends ReactiveCrudRepository<Treatment, Long> {

    Flux<Treatment> findByMedicalCaseId(Long medicalCaseId);

    Flux<Treatment> findByVeterinarianId(Long veterinarianId);

    Flux<Treatment> findByPetId(Long petId);

    @Query("""
            SELECT t.* FROM treatment t
            INNER JOIN medical_case mc ON mc.id = t.medical_case_id
            WHERE (:caseId IS NULL OR t.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR t.pet_id = :petId)
            AND (:vetId IS NULL OR t.veterinarian_id = :vetId)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Treatment> search(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId,
            int limit,
            int offset);

    @Query("""
            SELECT COUNT(*) FROM treatment t
            INNER JOIN medical_case mc ON mc.id = t.medical_case_id
            WHERE (:caseId IS NULL OR t.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR t.pet_id = :petId)
            AND (:vetId IS NULL OR t.veterinarian_id = :vetId)
            """)
    Mono<Long> countFiltered(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId);
}
