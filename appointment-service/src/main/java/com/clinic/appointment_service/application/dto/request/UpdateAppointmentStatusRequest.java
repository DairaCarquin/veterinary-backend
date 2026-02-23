package com.clinic.appointment_service.application.dto.request;

import com.clinic.appointment_service.domain.model.AppointmentStatus;

import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    private AppointmentStatus status;
}