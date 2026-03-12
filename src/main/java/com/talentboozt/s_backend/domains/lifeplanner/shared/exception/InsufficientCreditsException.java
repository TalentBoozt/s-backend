package com.talentboozt.s_backend.domains.lifeplanner.shared.exception;

public class InsufficientCreditsException extends RuntimeException {
    public InsufficientCreditsException(String message) {
        super(message);
    }
}
