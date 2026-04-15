package com.clinic.appointment_service.application.dto.request;

import com.clinic.appointment_service.domain.model.AppointmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    @NotNull(message = "El estado es obligatorio")
    private AppointmentStatus status;
}
