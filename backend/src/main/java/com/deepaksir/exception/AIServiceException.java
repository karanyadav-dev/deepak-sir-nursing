package com.deepaksir.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AIServiceException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public AIServiceException(String message) {
        this(message, HttpStatus.SERVICE_UNAVAILABLE, "AI_SERVICE_ERROR");
    }
    
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.SERVICE_UNAVAILABLE;
        this.errorCode = "AI_SERVICE_ERROR";
    }
    
    public AIServiceException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}