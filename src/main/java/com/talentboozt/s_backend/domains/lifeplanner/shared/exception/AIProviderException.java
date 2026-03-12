package com.talentboozt.s_backend.domains.lifeplanner.shared.exception;

public class AIProviderException extends RuntimeException {
    public AIProviderException(String message) {
        super(message);
    }

    public AIProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
