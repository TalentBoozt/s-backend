package com.talentboozt.s_backend.domains.edu.exception;

import org.springframework.http.HttpStatus;

public class EduLimitExceededException extends EduBaseException {
    public EduLimitExceededException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "LIMIT_EXCEEDED");
    }
}
