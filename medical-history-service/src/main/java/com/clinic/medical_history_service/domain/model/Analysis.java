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
@Table("analysis")
public class Analysis {

    @Id
    private Long id;

    private Long medicalCaseId;
    private Long petId;
    private Long veterinarianId;

    private String description;
    private String result;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}