package com.clinic.appointment_service.infrastructure.adapter.out;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.clinic.appointment_service.domain.model.Appointment;
import com.clinic.appointment_service.domain.port.AppointmentRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcAppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final R2dbcAppointmentRepository repository;

    @Override
    public Mono<Appointment> save(Appointment appointment) {
        return repository.save(appointment);
    }

    @Override
    public Flux<Appointment> search(Long petId,
            Long clientId,
            Long veterinarianId,
            String status,
            int limit,
            int offset) {
        return repository.search(petId, clientId, veterinarianId, status, null, limit, offset);
    }

    @Override
    public Flux<Appointment> findByClientId(Long clientId) {
        return repository.findByClientId(clientId);
    }

    @Override
    public Flux<Appointment> findByVeterinarianId(Long veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId);
    }

    @Override
    public Flux<Appointment> findByPetId(Long petId) {
        return repository.findByPetId(petId);
    }

    @Override
    public Mono<Long> countEnabled() {
        return repository.countEnabled();
    }

    @Override
    public Mono<Appointment> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Long> countVetConflicts(Long vetId, LocalDateTime date) {
        return repository.countVetConflicts(vetId, date);
    }

    @Override
    public Mono<Long> countFiltered(Long clientId, Long veterinarianId) {
        return repository.countFiltered(clientId, veterinarianId);
    }
}