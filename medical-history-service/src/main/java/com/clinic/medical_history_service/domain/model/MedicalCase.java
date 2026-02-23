package com.clinic.medical_history_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("medical_case")
public class MedicalCase {

    @Id
    private Long id;

    private Long appointmentId;
    private Long petId;
    private Long clientId;
    private Long veterinarianId;

    private LocalDateTime createdAt;
}