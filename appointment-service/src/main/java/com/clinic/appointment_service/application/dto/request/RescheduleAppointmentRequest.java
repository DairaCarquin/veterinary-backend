package com.clinic.appointment_service.application.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RescheduleAppointmentRequest {
    private LocalDateTime appointmentDate;
    private Long veterinarianId;
}