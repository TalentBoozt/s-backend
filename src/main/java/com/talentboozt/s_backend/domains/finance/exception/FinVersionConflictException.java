package com.talentboozt.s_backend.domains.finance.exception;

public class FinVersionConflictException extends RuntimeException {
    public FinVersionConflictException(String message) {
        super(message);
    }
}
