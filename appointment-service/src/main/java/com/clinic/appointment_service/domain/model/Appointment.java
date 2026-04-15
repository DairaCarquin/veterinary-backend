package com.clinic.appointment_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "La mascota es obligatoria")
    private Long petId;
    @NotNull(message = "El cliente es obligatorio")
    private Long clientId;
    @NotNull(message = "El veterinario es obligatorio")
    private Long veterinarianId;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @Future(message = "La cita debe programarse en el futuro")
    private LocalDateTime appointmentDate;

    private AppointmentStatus status;
    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 500, message = "El motivo no debe exceder 500 caracteres")
    private String reason;

    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
