package com.vet.security.domain.exception;

/**
 * Códigos de error estandarizados para el sistema
 */
public enum ErrorCode {
    
    // Códigos de Usuario (USER_)
    USER_001("USER_001", "Usuario ya existe"),
    USER_002("USER_002", "Usuario no encontrado"),
    USER_003("USER_003", "Username es obligatorio"),
    USER_004("USER_004", "Password es obligatorio"),
    USER_005("USER_005", "Password debe tener mínimo 8 caracteres"),
    USER_006("USER_006", "Datos incompletos"),
    
    // Códigos de Rol (ROLE_)
    ROLE_001("ROLE_001", "Rol inválido"),
    ROLE_002("ROLE_002", "Rol no encontrado"),
    ROLE_003("ROLE_003", "Rol ya existe"),
    ROLE_004("ROLE_004", "Rol protegido del sistema no puede ser modificado"),
    ROLE_005("ROLE_005", "Rol está asociado a usuarios"),
    ROLE_006("ROLE_006", "Nombre del rol debe estar en mayúsculas"),
    ROLE_007("ROLE_007", "Nombre del rol debe seguir convención ROLE_*"),
    
    // Códigos de Autenticación (AUTH_)
    AUTH_001("AUTH_001", "Acceso denegado"),
    AUTH_002("AUTH_002", "Token inválido o expirado"),
    AUTH_003("AUTH_003", "Credenciales inválidas"),
    AUTH_004("AUTH_004", "Usuario deshabilitado"),
    
    // Códigos Generales (GEN_)
    GEN_001("GEN_001", "Error interno del servidor"),
    GEN_002("GEN_002", "Validación de datos falló"),
    GEN_003("GEN_003", "Recurso no encontrado");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
