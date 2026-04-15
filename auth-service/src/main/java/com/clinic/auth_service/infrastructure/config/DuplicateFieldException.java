package com.clinic.auth_service.infrastructure.config;

import java.util.Map;

import lombok.Getter;

@Getter
public class DuplicateFieldException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public DuplicateFieldException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
