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
            SELECT r.* FROM referral r
            INNER JOIN medical_case mc ON mc.id = r.medical_case_id
            WHERE (:caseId IS NULL OR r.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR r.pet_id = :petId)
            AND (:vetId IS NULL OR r.veterinarian_id = :vetId)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Referral> search(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId,
            int limit,
            int offset);

    @Query("""
            SELECT COUNT(*) FROM referral r
            INNER JOIN medical_case mc ON mc.id = r.medical_case_id
            WHERE (:caseId IS NULL OR r.medical_case_id = :caseId)
            AND (:clientId IS NULL OR mc.client_id = :clientId)
            AND (:petId IS NULL OR r.pet_id = :petId)
            AND (:vetId IS NULL OR r.veterinarian_id = :vetId)
            """)
    Mono<Long> countFiltered(
            Long caseId,
            Long clientId,
            Long petId,
            Long vetId);
}
