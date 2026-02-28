package com.clinic.appointment_service.application.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RescheduleRequest {

    private LocalDateTime newDate;
    private Long newVeterinarianId;
}