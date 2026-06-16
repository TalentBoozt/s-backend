package com.talentboozt.s_backend.domains.finance.analytics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormulaEngineTest {

    private FormulaEngine formulaEngine;

    @BeforeEach
    void setUp() {
        formulaEngine = new FormulaEngine();
    }

    @Test
    void calculate_shouldReturnZeroForNullFormula() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act
        Double result = formulaEngine.calculate(null, variables);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void calculate_shouldReturnZeroForBlankFormula() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act
        Double result = formulaEngine.calculate("   ", variables);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void calculate_shouldEvaluateSimpleArithmetic() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act
        Double result = formulaEngine.calculate("10.0 + 5.0 * 2.0", variables);

        // Assert
        assertThat(result).isEqualTo(20.0);
    }

    @Test
    void calculate_shouldEvaluateWithVariables() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();
        variables.put("usersCount", 100.0);
        variables.put("pricePerUser", 5.0);

        // Act
        Double result = formulaEngine.calculate("usersCount * pricePerUser", variables);

        // Assert
        assertThat(result).isEqualTo(500.0);
    }

    @Test
    void calculate_shouldEvaluateTernaryTrue() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();
        variables.put("usersCount", 60.0);

        // Act
        Double result = formulaEngine.calculate("usersCount > 50 ? 200.0 : 100.0", variables);

        // Assert
        assertThat(result).isEqualTo(200.0);
    }

    @Test
    void calculate_shouldEvaluateTernaryFalse() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();
        variables.put("usersCount", 40.0);

        // Act
        Double result = formulaEngine.calculate("usersCount > 50 ? 200.0 : 100.0", variables);

        // Assert
        assertThat(result).isEqualTo(100.0);
    }

    @Test
    void calculate_shouldReturnZeroForNonNumericResult() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act
        Double result = formulaEngine.calculate("'hello'", variables);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void calculate_shouldThrowExceptionForSyntaxError() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act & Assert
        assertThatThrownBy(() -> formulaEngine.calculate("usersCount * +", variables))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid formula");
    }

    @Test
    void calculate_shouldThrowExceptionForMissingVariable() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act & Assert
        assertThatThrownBy(() -> formulaEngine.calculate("usersCount * 2", variables))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid formula");
    }

    @Test
    void calculate_shouldReturnInfinityForDoubleDivisionByZero() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act
        Double result = formulaEngine.calculate("1.0 / 0.0", variables);

        // Assert
        assertThat(result).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    void calculate_shouldThrowExceptionForIntegerDivisionByZero() {
        // Arrange
        Map<String, Double> variables = new HashMap<>();

        // Act & Assert
        assertThatThrownBy(() -> formulaEngine.calculate("1 / 0", variables))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid formula");
    }

    @Test
    void extractVariables_shouldReturnEmptySetForNullFormula() {
        // Act
        Set<String> variables = formulaEngine.extractVariables(null);

        // Assert
        assertThat(variables).isEmpty();
    }

    @Test
    void extractVariables_shouldReturnEmptySetForBlankFormula() {
        // Act
        Set<String> variables = formulaEngine.extractVariables("   ");

        // Assert
        assertThat(variables).isEmpty();
    }

    @Test
    void extractVariables_shouldExtractVariablesFromNormalFormula() {
        // Act
        Set<String> variables = formulaEngine.extractVariables("usersCount * pricePerUser");

        // Assert
        assertThat(variables).containsExactlyInAnyOrder("usersCount", "pricePerUser");
    }

    @Test
    void extractVariables_shouldExcludeTypeAndMethodReferences() {
        // Act
        Set<String> variables = formulaEngine.extractVariables("T(java.lang.Math).max(usersCount, 10.0)");

        // Assert
        assertThat(variables).containsExactly("usersCount");
    }

    @Test
    void extractVariables_shouldThrowExceptionForSyntaxError() {
        // Act & Assert
        assertThatThrownBy(() -> formulaEngine.extractVariables("usersCount * +"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Parsing error");
    }
}
