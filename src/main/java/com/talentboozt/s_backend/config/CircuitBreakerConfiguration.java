package com.talentboozt.s_backend.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit breaker configuration for external service calls
 * Prevents cascading failures and improves system resilience
 */
@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Open circuit after 50% failure rate
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before half-open
                .slidingWindowSize(10) // Last 10 calls
                .minimumNumberOfCalls(5) // Minimum calls before calculating failure rate
                .permittedNumberOfCallsInHalfOpenState(3) // Allow 3 calls in half-open
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(Exception.class)
                .build();

        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public CircuitBreaker externalApiCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(60)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .build();
        return registry.circuitBreaker("externalApi", config);
    }

    @Bean
    public CircuitBreaker emailServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(45))
                .slidingWindowSize(15)
                .minimumNumberOfCalls(5)
                .build();
        return registry.circuitBreaker("emailService", config);
    }

    @Bean
    public CircuitBreaker paymentServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(40) // Lower threshold for payment services
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(20)
                .minimumNumberOfCalls(5)
                .build();
        return registry.circuitBreaker("paymentService", config);
    }
}
