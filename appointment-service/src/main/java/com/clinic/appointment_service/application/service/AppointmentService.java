package com.clinic.appointment_service.application.service;

import java.time.LocalDateTime;

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

        public Mono<Appointment> create(Appointment appointment,
                        String role,
                        Long authenticatedUserId) {

                if (appointment.getPetId() == null ||
                                appointment.getClientId() == null ||
                                appointment.getVeterinarianId() == null) {

                        return Mono.error(
                                        new BusinessException(HttpStatus.BAD_REQUEST,
                                                        "Datos de cita incompletos"));
                }

                if (role.equals("CLIENT")) {
                        if (!appointment.getClientId().equals(authenticatedUserId)) {
                                return Mono.error(
                                                new BusinessException(HttpStatus.FORBIDDEN,
                                                                "No puede crear citas para otro cliente"));
                        }
                }

                appointment.setStatus(AppointmentStatus.PENDING);
                appointment.setCreatedAt(LocalDateTime.now());

                return repository.save(appointment)
                                .doOnSuccess(saved -> kafkaTemplate.send(
                                                "appointment-created-topic",
                                                new AppointmentCreatedEvent(
                                                                saved.getId(),
                                                                saved.getPetId(),
                                                                saved.getClientId(),
                                                                saved.getVeterinarianId())));
        }

        public Flux<Appointment> findAll(String role, Long userId) {

                if (role == null) {
                        return Flux.error(
                                        new BusinessException(HttpStatus.UNAUTHORIZED,
                                                        "Usuario no autenticado"));
                }

                if (role.equals("ADMIN")) {
                        return repository.findAll()
                                        .switchIfEmpty(Flux.error(
                                                        new BusinessException(HttpStatus.NOT_FOUND,
                                                                        "No existen citas registradas")));
                }

                if (role.equals("VETERINARY")) {
                        return repository.findByVeterinarianId(userId)
                                        .switchIfEmpty(Flux.error(
                                                        new BusinessException(HttpStatus.NOT_FOUND,
                                                                        "No tiene citas asignadas")));
                }

                if (role.equals("CLIENT")) {
                        return repository.findByClientId(userId)
                                        .switchIfEmpty(Flux.error(
                                                        new BusinessException(HttpStatus.NOT_FOUND,
                                                                        "No tiene citas registradas")));
                }

                return Flux.error(
                                new BusinessException(HttpStatus.FORBIDDEN,
                                                "Acceso no permitido"));
        }

        public Flux<Appointment> findByPet(Long petId) {

                if (petId == null) {
                        return Flux.error(
                                        new BusinessException(HttpStatus.BAD_REQUEST,
                                                        "PetId es obligatorio"));
                }

                return repository.findByPetId(petId)
                                .switchIfEmpty(Flux.error(
                                                new BusinessException(HttpStatus.NOT_FOUND,
                                                                "No existen citas para esta mascota")));
        }

        public Mono<Appointment> updateStatus(Long id,
                        AppointmentStatus newStatus,
                        String role,
                        Long userId) {

                return repository.findById(id)
                                .switchIfEmpty(Mono.error(
                                                new BusinessException(HttpStatus.NOT_FOUND,
                                                                "Cita no encontrada")))
                                .flatMap(appointment -> {

                                        if (role.equals("VETERINARY") &&
                                                        !appointment.getVeterinarianId().equals(userId)) {

                                                return Mono.error(
                                                                new BusinessException(HttpStatus.FORBIDDEN,
                                                                                "No puede modificar citas que no le pertenecen"));
                                        }

                                        validateStatusTransition(
                                                        appointment.getStatus(),
                                                        newStatus);

                                        appointment.setStatus(newStatus);

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
                                });
        }

        private void validateStatusTransition(AppointmentStatus current,
                        AppointmentStatus next) {

                if (current == AppointmentStatus.PENDING) {
                        if (next != AppointmentStatus.RESCHEDULED &&
                                        next != AppointmentStatus.ATTENDED) {
                                throw new BusinessException(HttpStatus.BAD_REQUEST,
                                                "PENDING solo puede cambiar a RESCHEDULED o ATTENDED");
                        }
                }

                else if (current == AppointmentStatus.RESCHEDULED) {
                        if (next != AppointmentStatus.ATTENDED &&
                                        next != AppointmentStatus.CANCELLED) {
                                throw new BusinessException(HttpStatus.BAD_REQUEST,
                                                "RESCHEDULED solo puede cambiar a ATTENDED o CANCELLED");
                        }
                }

                else if (current == AppointmentStatus.ATTENDED) {
                        if (next != AppointmentStatus.PAID) {
                                throw new BusinessException(HttpStatus.BAD_REQUEST,
                                                "ATTENDED solo puede cambiar a PAID");
                        }
                }

                else if (current == AppointmentStatus.CANCELLED ||
                                current == AppointmentStatus.PAID) {

                        throw new BusinessException(HttpStatus.BAD_REQUEST,
                                        "Este estado es final y no puede modificarse");
                }
        }
}