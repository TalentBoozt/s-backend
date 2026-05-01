package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.exception.FinValidationException;
import com.talentboozt.s_backend.domains.finance_planning.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinValidatorService {
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    public void validate(Object entity) {
        if (entity instanceof FinPricingModel pricing) {
            validatePricing(pricing);
        } else if (entity instanceof FinSalesPlan sales) {
            validateSales(sales);
        } else if (entity instanceof FinBudget budget) {
            validateBudget(budget);
        } else if (entity instanceof FinAssumption assumption) {
            validateAssumption(assumption);
        }
    }

    private void validatePricing(FinPricingModel pricing) {
        if (pricing.getPrice() != null && pricing.getPrice() < 0) {
            throw new FinValidationException("Price cannot be negative");
        }
        if (pricing.getCostPerUser() != null && pricing.getCostPerUser() < 0) {
            throw new FinValidationException("Cost per user cannot be negative");
        }
    }

    private void validateSales(FinSalesPlan sales) {
        if (sales.getMonth() != null && !MONTH_PATTERN.matcher(sales.getMonth()).matches()) {
            throw new FinValidationException("Invalid month format. Expected YYYY-MM");
        }
        if (sales.getUserCounts() != null) {
            for (Map.Entry<String, Integer> entry : sales.getUserCounts().entrySet()) {
                if (entry.getValue() != null && entry.getValue() < 0) {
                    throw new FinValidationException("User count for tier " + entry.getKey() + " cannot be negative");
                }
            }
        }
    }

    private void validateBudget(FinBudget budget) {
        if (budget.getMonthlyAllocations() != null) {
            for (Map.Entry<String, Double> entry : budget.getMonthlyAllocations().entrySet()) {
                if (!MONTH_PATTERN.matcher(entry.getKey()).matches()) {
                    throw new FinValidationException("Invalid month format in budget allocations: " + entry.getKey());
                }
                if (entry.getValue() != null && entry.getValue() < 0) {
                    throw new FinValidationException("Budget allocation cannot be negative");
                }
            }
        }
        // Formula validation placeholder - in a real system, we'd dry-run the SpEL expression
        if (budget.getFormula() != null && budget.getFormula().contains(";") ) {
            throw new FinValidationException("Invalid formula syntax: potentially malicious characters detected");
        }
    }

    private void validateAssumption(FinAssumption assumption) {
        if (assumption.getKey() == null || assumption.getKey().isEmpty()) {
            throw new FinValidationException("Assumption key is required");
        }
    }
}
