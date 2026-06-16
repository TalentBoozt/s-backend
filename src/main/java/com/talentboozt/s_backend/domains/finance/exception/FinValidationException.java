package com.talentboozt.s_backend.domains.finance.exception;

public class FinValidationException extends RuntimeException {
    public FinValidationException(String message) {
        super(message);
    }
}
