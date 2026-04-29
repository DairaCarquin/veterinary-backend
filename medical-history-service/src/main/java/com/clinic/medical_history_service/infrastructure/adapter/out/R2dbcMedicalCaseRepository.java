package com.clinic.medical_history_service.infrastructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.medical_history_service.domain.model.MedicalCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcMedicalCaseRepository
                extends ReactiveCrudRepository<MedicalCase, Long> {
        Mono<MedicalCase> findByAppointmentId(Long appointmentId);

        @Query("""
                SELECT * FROM medical_case
                WHERE (:appointmentId IS NULL OR appointment_id = :appointmentId)
                AND (:clientId IS NULL OR client_id = :clientId)
                AND (:petId IS NULL OR pet_id = :petId)
                AND (:veterinarianId IS NULL OR veterinarian_id = :veterinarianId)
                ORDER BY created_at DESC
                LIMIT :limit OFFSET :offset
                """)
        Flux<MedicalCase> search(
                Long appointmentId,
                Long clientId,
                Long petId,
                Long veterinarianId,
                int limit,
                int offset);

        @Query("""
                SELECT COUNT(*) FROM medical_case
                WHERE (:appointmentId IS NULL OR appointment_id = :appointmentId)
                AND (:clientId IS NULL OR client_id = :clientId)
                AND (:petId IS NULL OR pet_id = :petId)
                AND (:veterinarianId IS NULL OR veterinarian_id = :veterinarianId)
                """)
        Mono<Long> countFiltered(
                Long appointmentId,
                Long clientId,
                Long petId,
                Long veterinarianId);

        @Query("""
                SELECT id FROM clients
                WHERE user_id = :userId
                AND enabled = true
                LIMIT 1
                """)
        Mono<Long> findClientIdByUserId(Long userId);
}
