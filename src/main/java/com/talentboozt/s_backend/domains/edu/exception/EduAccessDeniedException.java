package com.talentboozt.s_backend.domains.edu.exception;

import org.springframework.http.HttpStatus;

public class EduAccessDeniedException extends EduBaseException {
    public EduAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
