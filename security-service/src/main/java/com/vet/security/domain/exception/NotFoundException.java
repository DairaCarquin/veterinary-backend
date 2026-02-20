package com.vet.security.domain.exception;

public class NotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public NotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.GEN_003;
    }
    
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public NotFoundException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
