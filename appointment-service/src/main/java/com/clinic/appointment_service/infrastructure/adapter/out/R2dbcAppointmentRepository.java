package com.clinic.appointment_service.infrastructure.adapter.out;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.appointment_service.domain.model.Appointment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcAppointmentRepository extends ReactiveCrudRepository<Appointment, Long> {

        Flux<Appointment> findByClientId(Long clientId);

        Flux<Appointment> findByVeterinarianId(Long veterinarianId);

        Flux<Appointment> findByPetId(Long petId);

        @Query("""
                        SELECT * FROM appointments
                        WHERE enabled = true
                        AND (:clientId IS NULL OR client_id = :clientId)
                        AND (:veterinarianId IS NULL OR veterinarian_id = :veterinarianId)
                        AND (:petId IS NULL OR pet_id = :petId)
                        AND (:status IS NULL OR status = :status)
                        AND (:date IS NULL OR DATE(appointment_date) = DATE(:date))
                        LIMIT :limit OFFSET :offset
                        """)
        Flux<Appointment> search(Long clientId,
                        Long veterinarianId,
                        Long petId,
                        String status,
                        LocalDateTime date,
                        int limit,
                        int offset);

        @Query("""
                        SELECT COUNT(*) FROM appointments WHERE enabled = true
                        """)
        Mono<Long> countEnabled();

        @Query("""
                        SELECT COUNT(*) FROM appointments
                        WHERE veterinarian_id = :vetId
                        AND enabled = true
                        AND appointment_date = :date
                        """)
        Mono<Long> countVetConflicts(Long vetId, LocalDateTime date);

        @Query("""
                        SELECT COUNT(*) FROM appointments
                        WHERE enabled = true
                        AND (:clientId IS NULL OR client_id = :clientId)
                        AND (:veterinarianId IS NULL OR veterinarian_id = :veterinarianId)
                        """)
        Mono<Long> countFiltered(Long clientId,
                        Long veterinarianId);
}