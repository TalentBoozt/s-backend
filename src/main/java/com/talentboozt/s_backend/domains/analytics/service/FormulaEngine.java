package com.talentboozt.s_backend.domains.analytics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FormulaEngine {
    private final ExpressionParser parser = new SpelExpressionParser();

    public Double calculate(String formula, Map<String, Double> variables) {
        if (formula == null || formula.trim().isEmpty()) {
            return 0.0;
        }

        try {
            StandardEvaluationContext context = new StandardEvaluationContext(variables);
            context.addPropertyAccessor(new MapAccessor());
            
            Expression exp = parser.parseExpression(formula);
            Object result = exp.getValue(context);
            
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
            return 0.0;
        } catch (Exception e) {
            log.error("Formula evaluation failed: {} with variables: {}", formula, variables, e);
            throw new RuntimeException("Invalid formula: " + e.getMessage());
        }
    }

    public Set<String> extractVariables(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            return new HashSet<>();
        }

        try {
            Expression exp = parser.parseExpression(formula);
            Set<String> variables = new HashSet<>();
            if (exp instanceof SpelExpression) {
                SpelNode root = ((SpelExpression) exp).getAST();
                findVariables(root, variables);
            }
            return variables;
        } catch (Exception e) {
            log.error("Failed to extract variables from formula: {}", formula, e);
            throw new RuntimeException("Parsing error: " + e.getMessage());
        }
    }

    private void findVariables(SpelNode node, Set<String> variables) {
        if (node == null) return;
        
        // In SpEL AST, property/field references are the variables in our case
        String nodeString = node.toStringAST();
        
        // We only want leaf-level property references (not methods or complex objects)
        if (node.getClass().getSimpleName().equals("PropertyOrFieldReference")) {
            variables.add(nodeString);
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            findVariables(node.getChild(i), variables);
        }
    }
}
