package com.talentboozt.s_backend.domains.edu.exception;

import org.springframework.http.HttpStatus;

public class EduBadRequestException extends EduBaseException {
    public EduBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
}
