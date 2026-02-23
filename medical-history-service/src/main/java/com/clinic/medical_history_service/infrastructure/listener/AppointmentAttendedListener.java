package com.clinic.medical_history_service.infrastructure.listener;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.clinic.medical_history_service.domain.event.AppointmentAttendedEvent;
import com.clinic.medical_history_service.domain.model.MedicalCase;
import com.clinic.medical_history_service.domain.port.MedicalCaseRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentAttendedListener {

    private final MedicalCaseRepositoryPort repository;

    @KafkaListener(topics = "appointment-attended-topic", containerFactory = "kafkaListenerContainerFactory")
    public void handle(AppointmentAttendedEvent event) {

        MedicalCase medicalCase = MedicalCase.builder()
                .appointmentId(event.getAppointmentId())
                .petId(event.getPetId())
                .clientId(event.getClientId())
                .veterinarianId(event.getVeterinarianId())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(medicalCase).subscribe();
    }
}