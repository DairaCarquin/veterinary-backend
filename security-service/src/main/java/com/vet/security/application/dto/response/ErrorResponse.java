package com.vet.security.application.dto.response;

import java.time.Instant;

/**
 * Respuesta estandarizada de error
 */
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String code,
        String path
) {
    public static ErrorResponse of(int status, String error, String message, String code, String path) {
        return new ErrorResponse(
                Instant.now().toString(),
                status,
                error,
                message,
                code,
                path
        );
    }
}
