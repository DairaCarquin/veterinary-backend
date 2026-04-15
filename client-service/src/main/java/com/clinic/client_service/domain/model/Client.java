package com.clinic.client_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("clients")
public class Client {

    @Id
    private Long id;

    private Long userId;
    private String username;

    @Email(message = "El email debe ser valido")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@masterdog\\.com$", message = "El email debe pertenecer al dominio @masterdog.com")
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "El telefono debe tener exactamente 9 digitos")
    private String phone;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 digitos")
    private String dni;

    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String firstName;

    @Size(max = 100, message = "El apellido no debe exceder 100 caracteres")
    private String lastName;

    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
