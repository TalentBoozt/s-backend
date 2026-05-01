package com.talentboozt.s_backend.domains.analytics.service;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FormulaEngineTest {

    private final FormulaEngine engine = new FormulaEngine();

    @Test
    void shouldExtractVariables() {
        String formula = "revenue - cost * (tax + 0.1) / count";
        Set<String> variables = engine.extractVariables(formula);
        
        assertTrue(variables.contains("revenue"));
        assertTrue(variables.contains("cost"));
        assertTrue(variables.contains("tax"));
        assertTrue(variables.contains("count"));
        assertEquals(4, variables.size());
    }

    @Test
    void shouldExtractVariablesFromComplexFormula() {
        String formula = "IF(revenue > 1000, revenue * 0.9, revenue * 0.95) + bonus";
        Set<String> variables = engine.extractVariables(formula);
        
        assertTrue(variables.contains("revenue"));
        assertTrue(variables.contains("bonus"));
        assertEquals(2, variables.size());
    }

    @Test
    void shouldCalculateSimpleFormula() {
        String formula = "a + b * c";
        Map<String, Double> vars = Map.of("a", 10.0, "b", 5.0, "c", 2.0);
        
        Double result = engine.calculate(formula, vars);
        assertEquals(20.0, result);
    }

    @Test
    void shouldHandleNumericConstants() {
        String formula = "a * 1.5 + 100";
        Map<String, Double> vars = Map.of("a", 10.0);
        
        Double result = engine.calculate(formula, vars);
        assertEquals(115.0, result);
    }

    @Test
    void shouldThrowErrorForInvalidFormula() {
        String formula = "a + (b * c"; // Unclosed paren
        Map<String, Double> vars = Map.of("a", 1.0, "b", 2.0, "c", 3.0);
        
        assertThrows(RuntimeException.class, () -> engine.calculate(formula, vars));
    }
}
