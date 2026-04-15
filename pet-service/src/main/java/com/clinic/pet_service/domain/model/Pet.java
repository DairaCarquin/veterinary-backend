package com.clinic.pet_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String name;
    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 60, message = "La especie no debe exceder 60 caracteres")
    private String species;
    @NotBlank(message = "La raza es obligatoria")
    @Size(max = 80, message = "La raza no debe exceder 80 caracteres")
    private String breed;
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer age;
    @NotNull(message = "El propietario es obligatorio")
    private Long ownerId;

    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
