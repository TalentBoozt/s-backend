package com.talentboozt.s_backend.domains.edu.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class EduBaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public EduBaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
