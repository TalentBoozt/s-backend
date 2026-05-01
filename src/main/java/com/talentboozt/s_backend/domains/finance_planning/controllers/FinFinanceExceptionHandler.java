package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.exception.FinVersionConflictException;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = FinFinanceController.class)
public class FinFinanceExceptionHandler {

    @ExceptionHandler(FinVersionConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleVersionConflict(FinVersionConflictException ex) {
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(false)
                .error("VERSION_CONFLICT")
                .build());
    }

    @ExceptionHandler(com.talentboozt.s_backend.domains.finance_planning.exception.FinValidationException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> handleValidation(
            com.talentboozt.s_backend.domains.finance_planning.exception.FinValidationException ex) {
        return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "error", "VALIDATION_ERROR",
                "message", ex.getMessage()
        ));
    }
}
