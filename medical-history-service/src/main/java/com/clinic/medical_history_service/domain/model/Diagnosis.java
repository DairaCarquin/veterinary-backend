package com.clinic.medical_history_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("diagnosis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {

    @Id
    private Long id;

    private Long medicalCaseId;
    private Long petId;
    private Long veterinarianId;

    private String diagnosis;
    private String observations;

    private LocalDateTime createdAt;
}