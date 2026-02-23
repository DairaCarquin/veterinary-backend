package com.clinic.appointment_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentCreatedEvent {

    private Long appointmentId;
    private Long petId;
    private Long clientId;
    private Long veterinarianId;
}