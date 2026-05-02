package com.talentboozt.s_backend.domains.finance_planning.analytics.controller;

import com.talentboozt.s_backend.domains.finance_planning.analytics.service.FormulaEngine;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/formulas")
@RequiredArgsConstructor
public class FormulaController {

    private final FormulaEngine formulaEngine;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidationResult>> validateFormula(@RequestBody FormulaRequest request) {
        boolean isValid = true;
        String error = null;
        try {
            // Attempt to calculate with dummy values
            // In a real scenario, FormulaEngine would have a specific validate() method
            Double result = formulaEngine.calculate(request.getFormula(), request.getMockVariables());
        } catch (Exception e) {
            isValid = false;
            error = e.getMessage();
        }

        return ResponseEntity.ok(ApiResponse.success(new ValidationResult(isValid, error)));
    }

    @Data
    public static class FormulaRequest {
        private String formula;
        private Map<String, Double> mockVariables;
    }

    @Data
    @RequiredArgsConstructor
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
    }
}
