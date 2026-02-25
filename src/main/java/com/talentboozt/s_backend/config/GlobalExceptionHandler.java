package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.domains.common.dto.ApiErrorResponse;
import com.talentboozt.s_backend.domains.plat_courses.cfg.CouponValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Comprehensive global exception handler for production-grade error handling
 * Provides consistent error responses and proper logging
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CouponValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponException(CouponValidationException ex, WebRequest request) {
        logger.warn("Coupon validation failed: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input parameters");
        response.put("errors", errors);

        logger.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        logger.debug("JWT token expired");
        ApiErrorResponse error = new ApiErrorResponse(
                "Token has expired. Please refresh your token.",
                HttpStatus.UNAUTHORIZED.value(),
                "TOKEN_EXPIRED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleSignatureException(SignatureException ex) {
        logger.warn("Invalid JWT signature");
        ApiErrorResponse error = new ApiErrorResponse(
                "Invalid token signature",
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationCredentialsNotFoundException ex) {
        logger.debug("Authentication credentials not found");
        ApiErrorResponse error = new ApiErrorResponse(
                "Authentication required",
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_REQUIRED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                "Access denied. Insufficient permissions.",
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex) {
        logger.error("Database access error", ex);
        ApiErrorResponse error = new ApiErrorResponse(
                "Database operation failed. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "DATABASE_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiErrorResponse> handleTimeoutException(TimeoutException ex) {
        logger.error("Operation timeout", ex);
        ApiErrorResponse error = new ApiErrorResponse(
                "Request timeout. Please try again.",
                HttpStatus.REQUEST_TIMEOUT.value(),
                "TIMEOUT");
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.debug("No handler found for: {}", ex.getRequestURL());
        ApiErrorResponse error = new ApiErrorResponse(
                "Endpoint not found",
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        logger.debug("No static resource found: {}", ex.getResourcePath());
        ApiErrorResponse error = new ApiErrorResponse(
                "Resource not found",
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(com.talentboozt.s_backend.domains.community.exception.ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            com.talentboozt.s_backend.domains.community.exception.ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        // If the response is already committed, we can't write a JSON body
        // Also check if the request is looking for application/javascript (SockJS
        // fallback)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/javascript")) {
            logger.warn("Runtime exception during non-JSON request: {}", ex.getMessage());
            return null; // Let the container handle it or return empty
        }

        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage() != null ? ex.getMessage() : "An error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "RUNTIME_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex,
            WebRequest request) {
        logger.warn("Could not write JSON response (possibly response already committed or incompatible type): {}",
                ex.getMessage());
        return null; // Prevent infinite loop or secondary errors in ExceptionHandler
    }

    @ExceptionHandler(io.github.resilience4j.ratelimiter.RequestNotPermitted.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitException(
            io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        logger.warn("Rate limit exceeded");
        ApiErrorResponse error = new ApiErrorResponse(
                "Rate limit exceeded. Please try again later.",
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "RATE_LIMIT_EXCEEDED");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred", ex);
        ApiErrorResponse error = new ApiErrorResponse(
                "An unexpected error occurred. Please contact support if the problem persists.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
