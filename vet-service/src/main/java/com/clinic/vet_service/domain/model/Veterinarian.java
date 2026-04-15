package com.clinic.vet_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres")
    private String name;

    @Size(max = 120, message = "El apellido no debe exceder 120 caracteres")
    private String lastName;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 digitos")
    private String dni;

    @NotBlank(message = "La especialidad es obligatoria")
    @Pattern(regexp = "^[\\p{L}\\s'-]{1,100}$", message = "La especialidad solo puede contener letras")
    @Size(max = 100, message = "La especialidad no debe exceder 100 caracteres")
    private String specialty;

    @NotBlank(message = "El numero de licencia es obligatorio")
    @Pattern(regexp = "^[0-9]{5}$", message = "La licencia debe tener exactamente 5 digitos")
    private String licenseNumber;

    @Email(message = "El email debe ser valido")
    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@masterdog\\.com$", message = "El email debe pertenecer al dominio @masterdog.com")
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "El telefono debe tener exactamente 9 digitos")
    private String phone;

    private Boolean available;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
