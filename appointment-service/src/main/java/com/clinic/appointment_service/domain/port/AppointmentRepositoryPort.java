package com.clinic.appointment_service.domain.port;

import com.clinic.appointment_service.domain.model.Appointment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppointmentRepositoryPort {

    Mono<Appointment> save(Appointment appointment);

    Flux<Appointment> findAll();

    Flux<Appointment> findByClientId(Long clientId);

    Flux<Appointment> findByVeterinarianId(Long veterinarianId);

    Flux<Appointment> findByPetId(Long petId);

    Mono<Appointment> findById(Long id);
}