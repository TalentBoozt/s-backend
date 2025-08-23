package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.domains.common.dto.ApiErrorResponse;
import com.talentboozt.s_backend.domains.plat_courses.cfg.CouponValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponException(CouponValidationException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrorCode()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // You can add more handlers here (e.g., @ExceptionHandler(RuntimeException.class))
}
