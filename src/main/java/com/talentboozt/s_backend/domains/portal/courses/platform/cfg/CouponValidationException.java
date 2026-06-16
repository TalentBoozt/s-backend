package com.talentboozt.s_backend.domains.portal.courses.platform.cfg;

public class CouponValidationException extends RuntimeException {
    private final String errorCode;

    public CouponValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
