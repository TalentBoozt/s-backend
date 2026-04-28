package com.talentboozt.s_backend.domains.referral.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReferralException extends RuntimeException {
    public ReferralException(String message) {
        super(message);
    }
}
