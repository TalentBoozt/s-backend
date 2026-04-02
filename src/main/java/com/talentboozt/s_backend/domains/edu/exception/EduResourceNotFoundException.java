package com.talentboozt.s_backend.domains.edu.exception;

import org.springframework.http.HttpStatus;

public class EduResourceNotFoundException extends EduBaseException {
    public EduResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
