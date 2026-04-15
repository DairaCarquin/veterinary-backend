package com.clinic.appointment_service.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.clinic.appointment_service.domain.event.AppointmentAttendedEvent;
import com.clinic.appointment_service.domain.event.AppointmentCreatedEvent;
import com.clinic.appointment_service.domain.model.Appointment;
import com.clinic.appointment_service.domain.model.AppointmentStatus;
import com.clinic.appointment_service.domain.port.AppointmentRepositoryPort;
import com.clinic.appointment_service.infrastructure.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepositoryPort repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Appointment> create(Appointment appointment, String role, Long authenticatedUserId) {
        if ("CLIENT".equals(role) && !appointment.getClientId().equals(authenticatedUserId)) {
            return Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "No puede crear citas para otro cliente"));
        }

        if (appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            return Mono.error(new BusinessException(HttpStatus.BAD_REQUEST, "La cita debe programarse en el futuro"));
        }

        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setEnabled(true);
        appointment.setCreatedAt(LocalDateTime.now());

        return repository.countVetConflicts(appointment.getVeterinarianId(), appointment.getAppointmentDate())
                .flatMap(conflicts -> {
                    if (conflicts > 0) {
                        return Mono.error(new BusinessException(
                                HttpStatus.BAD_REQUEST,
                                "El veterinario ya tiene una cita en ese horario"));
                    }

                    return repository.save(appointment)
                            .doOnSuccess(saved -> kafkaTemplate.send(
                                    "appointment-created-topic",
                                    new AppointmentCreatedEvent(
                                            saved.getId(),
                                            saved.getPetId(),
                                            saved.getClientId(),
                                            saved.getVeterinarianId())));
                });
    }

    public Mono<Appointment> findById(Long id, String role, Long userId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada")))
                .flatMap(appointment -> validateOwnership(appointment, role, userId).thenReturn(appointment));
    }

    public Mono<Map<String, Object>> findAll(
            String role,
            Long userId,
            Long petId,
            Long veterinarianId,
            String status,
            LocalDateTime date,
            int page,
            int size) {

        int offset = page * size;

        Long clientFilter = "CLIENT".equals(role) ? userId : null;
        Long vetFilter = "VETERINARY".equals(role) ? userId : veterinarianId;

        return repository.search(clientFilter, vetFilter, petId, status, date, size, offset)
                .collectList()
                .zipWith(repository.countFiltered(clientFilter, vetFilter, petId, status, date))
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", tuple.getT1());
                    response.put("total", tuple.getT2());
                    response.put("page", page);
                    response.put("size", size);
                    return response;
                });
    }

    public Flux<Appointment> findByPet(Long petId) {
        if (petId == null) {
            return Flux.error(new BusinessException(HttpStatus.BAD_REQUEST, "PetId es obligatorio"));
        }

        return repository.findByPetId(petId)
                .switchIfEmpty(Flux.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "No existen citas para esta mascota")));
    }

    public Mono<Appointment> updateStatus(Long id, AppointmentStatus newStatus, String role, Long userId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada")))
                .flatMap(appointment -> validateOwnership(appointment, role, userId)
                        .then(Mono.defer(() -> {
                            validateStatusTransition(appointment.getStatus(), newStatus);

                            appointment.setStatus(newStatus);
                            appointment.setUpdatedAt(LocalDateTime.now());

                            return repository.save(appointment)
                                    .doOnSuccess(saved -> {
                                        if (newStatus == AppointmentStatus.ATTENDED) {
                                            kafkaTemplate.send(
                                                    "appointment-attended-topic",
                                                    new AppointmentAttendedEvent(
                                                            saved.getId(),
                                                            saved.getPetId(),
                                                            saved.getClientId(),
                                                            saved.getVeterinarianId()));
                                        }
                                    });
                        })));
    }

    public Mono<Appointment> reschedule(Long id, LocalDateTime newDate, Long newVetId, String role, Long userId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada")))
                .flatMap(appointment -> validateOwnership(appointment, role, userId)
                        .then(Mono.defer(() -> {
                            if (newDate.isBefore(LocalDateTime.now())) {
                                return Mono.error(new BusinessException(
                                        HttpStatus.BAD_REQUEST,
                                        "La nueva fecha debe estar en el futuro"));
                            }

                            Long vetToValidate = newVetId != null ? newVetId : appointment.getVeterinarianId();

                            return repository.countVetConflicts(vetToValidate, newDate)
                                    .flatMap(conflicts -> {
                                        if (conflicts > 0) {
                                            return Mono.error(new BusinessException(
                                                    HttpStatus.BAD_REQUEST,
                                                    "El veterinario ya tiene cita en ese horario"));
                                        }

                                        appointment.setAppointmentDate(newDate);

                                        if (newVetId != null) {
                                            appointment.setVeterinarianId(newVetId);
                                        }

                                        appointment.setStatus(AppointmentStatus.RESCHEDULED);
                                        appointment.setUpdatedAt(LocalDateTime.now());

                                        return repository.save(appointment);
                                    });
                        })));
    }

    public Mono<Void> toggleEnabled(Long id, boolean enabled) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada")))
                .flatMap(appointment -> {
                    appointment.setEnabled(enabled);
                    appointment.setUpdatedAt(LocalDateTime.now());
                    return repository.save(appointment);
                })
                .then();
    }

    private Mono<Void> validateOwnership(Appointment appointment, String role, Long userId) {
        if ("CLIENT".equals(role) && !appointment.getClientId().equals(userId)) {
            return Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "No puede acceder a esta cita"));
        }

        if ("VETERINARY".equals(role) && !appointment.getVeterinarianId().equals(userId)) {
            return Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "No puede acceder a esta cita"));
        }

        return Mono.empty();
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == AppointmentStatus.PENDING) {
            if (next != AppointmentStatus.RESCHEDULED
                    && next != AppointmentStatus.ATTENDED
                    && next != AppointmentStatus.CANCELLED) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "PENDING solo puede cambiar a RESCHEDULED, ATTENDED o CANCELLED");
            }
        } else if (current == AppointmentStatus.RESCHEDULED) {
            if (next != AppointmentStatus.ATTENDED && next != AppointmentStatus.CANCELLED) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "RESCHEDULED solo puede cambiar a ATTENDED o CANCELLED");
            }
        } else if (current == AppointmentStatus.ATTENDED) {
            if (next != AppointmentStatus.PAID) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "ATTENDED solo puede cambiar a PAID");
            }
        } else if (current == AppointmentStatus.CANCELLED || current == AppointmentStatus.PAID) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Este estado es final y no puede modificarse");
        }
    }
}
