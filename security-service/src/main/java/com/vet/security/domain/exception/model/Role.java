package com.vet.security.domain.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    
    // Roles protegidos del sistema que no se pueden eliminar ni renombrar
    // Siguiendo convención ROLE_*
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String VETERINARIO = "ROLE_VETERINARIO";
    public static final String CLIENTE = "ROLE_CLIENTE";
    
    public boolean isProtected() {
        return ADMIN.equals(name) || 
               VETERINARIO.equals(name) || 
               CLIENTE.equals(name);
    }
    
    /**
     * Valida que el nombre del rol siga la convención ROLE_*
     */
    public static boolean isValidRoleName(String name) {
        return name != null && name.startsWith("ROLE_");
    }
    
    /**
     * Normaliza el nombre del rol agregando ROLE_ si no lo tiene
     */
    public static String normalizeRoleName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim().toUpperCase();
        return trimmed.startsWith("ROLE_") ? trimmed : "ROLE_" + trimmed;
    }
}
