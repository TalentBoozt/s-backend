package com.talentboozt.s_backend.domains.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;

    public ApiErrorResponse(String message, int status, String errorCode) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }
}
