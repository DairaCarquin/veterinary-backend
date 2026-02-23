package com.clinic.appointment_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    private Long id;

    private Long petId;
    private Long clientId;
    private Long veterinarianId;

    private LocalDateTime appointmentDate;

    private AppointmentStatus status;
    private String reason;

    private LocalDateTime createdAt;
}