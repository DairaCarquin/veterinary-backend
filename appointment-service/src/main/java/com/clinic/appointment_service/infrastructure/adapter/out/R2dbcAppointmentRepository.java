package com.clinic.appointment_service.infrastructure.adapter.out;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.clinic.appointment_service.domain.model.Appointment;

import reactor.core.publisher.Flux;

public interface R2dbcAppointmentRepository
        extends ReactiveCrudRepository<Appointment, Long> {

    Flux<Appointment> findByClientId(Long clientId);

    Flux<Appointment> findByVeterinarianId(Long veterinarianId);

    Flux<Appointment> findByPetId(Long petId);
}