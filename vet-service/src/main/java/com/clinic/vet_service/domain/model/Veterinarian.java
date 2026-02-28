package com.clinic.vet_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("veterinarians")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinarian {

    @Id
    private Long id;

    private Long userId;
    private String username;
    private String name;
    private String specialty;
    private String licenseNumber;
    private String email;
    private Boolean available;

    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}