package com.vet.security.domain.exception;

public class BadRequestException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BadRequestException(String message) {
        super(message);
        this.errorCode = ErrorCode.GEN_002;
    }
    
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BadRequestException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
