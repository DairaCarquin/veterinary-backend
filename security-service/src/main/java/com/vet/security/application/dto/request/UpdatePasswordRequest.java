package com.vet.security.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = "Old password es obligatorio")
        String oldPassword,
        
        @NotBlank(message = "New password es obligatorio")
        @Size(min = 8, message = "New password debe tener mínimo 8 caracteres")
        String newPassword,
        
        String newDni // Solo requerido para usuarios con rol CLIENTE
) {
}
