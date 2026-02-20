package com.vet.security.domain.exception;

public class ConflictException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public ConflictException(String message) {
        super(message);
        this.errorCode = ErrorCode.GEN_002;
    }
    
    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public ConflictException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
