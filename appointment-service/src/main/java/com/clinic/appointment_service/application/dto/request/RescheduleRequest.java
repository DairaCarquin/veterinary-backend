package com.clinic.appointment_service.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RescheduleRequest {

    @NotNull(message = "La nueva fecha es obligatoria")
    @Future(message = "La nueva fecha debe estar en el futuro")
    private LocalDateTime newDate;
    private Long newVeterinarianId;
}
