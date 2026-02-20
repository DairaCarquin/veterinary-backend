package com.vet.security.domain.exception;

public class ForbiddenException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public ForbiddenException(String message) {
        super(message);
        this.errorCode = ErrorCode.AUTH_001;
    }
    
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public ForbiddenException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
