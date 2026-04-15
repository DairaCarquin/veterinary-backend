package com.clinic.appointment_service.domain.port;

import java.time.LocalDateTime;

import com.clinic.appointment_service.domain.model.Appointment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppointmentRepositoryPort {

    Mono<Appointment> save(Appointment appointment);

    Flux<Appointment> search(
            Long clientId,
            Long veterinarianId,
            Long petId,
            String status,
            LocalDateTime date,
            int limit,
            int offset);

    Flux<Appointment> findByClientId(Long clientId);

    Flux<Appointment> findByVeterinarianId(Long veterinarianId);

    Flux<Appointment> findByPetId(Long petId);

    Mono<Long> countEnabled();

    Mono<Appointment> findById(Long id);

    Mono<Long> countVetConflicts(Long vetId, LocalDateTime date);

    Mono<Long> countFiltered(Long clientId, Long veterinarianId, Long petId, String status, LocalDateTime date);
}
