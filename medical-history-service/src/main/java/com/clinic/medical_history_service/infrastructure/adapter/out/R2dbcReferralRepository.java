package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.clinic.medical_history_service.domain.model.Referral;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcReferralRepository extends ReactiveCrudRepository<Referral, Long> {

    Flux<Referral> findByMedicalCaseId(Long medicalCaseId);

    Flux<Referral> findByVeterinarianId(Long veterinarianId);

    Flux<Referral> findByPetId(Long petId);

    @Query("""
            SELECT * FROM referral
            WHERE (:caseId IS NULL OR medical_case_id = :caseId)
            AND (:petId IS NULL OR pet_id = :petId)
            AND (:vetId IS NULL OR veterinarian_id = :vetId)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Referral> search(
            Long caseId,
            Long petId,
            Long vetId,
            int limit,
            int offset);

    @Query("""
            SELECT COUNT(*) FROM referral
            WHERE (:caseId IS NULL OR medical_case_id = :caseId)
            AND (:petId IS NULL OR pet_id = :petId)
            AND (:vetId IS NULL OR veterinarian_id = :vetId)
            """)
    Mono<Long> countFiltered(
            Long caseId,
            Long petId,
            Long vetId);
}