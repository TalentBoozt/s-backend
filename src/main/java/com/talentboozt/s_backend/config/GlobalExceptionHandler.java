package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.domains.common.dto.ApiErrorResponse;
import com.talentboozt.s_backend.domains.edu.exception.EduBaseException;
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
import org.springframework.web.context.request.ServletWebRequest;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed for request {}: {}", getRequestPath(request), errors);
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid input parameters",
                "VALIDATION_FAILED",
                request,
                errors
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        logger.debug("JWT token expired for request {}", getRequestPath(request));
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Token has expired. Please refresh your token.",
                "TOKEN_EXPIRED",
                request
        );
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleSignatureException(SignatureException ex, WebRequest request) {
        logger.warn("Invalid JWT signature for request {}", getRequestPath(request));
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid token signature",
                "INVALID_TOKEN",
                request
        );
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationCredentialsNotFoundException ex, WebRequest request) {
        logger.debug("Authentication credentials not found for request {}", getRequestPath(request));
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication required",
                "AUTHENTICATION_REQUIRED",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.warn("Access denied for request {}: {}", getRequestPath(request), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied. Insufficient permissions.",
                "ACCESS_DENIED",
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException ex, WebRequest request) {
        logger.error("Database access error for request {}", getRequestPath(request), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A database error occurred. Please try again later.",
                "DATABASE_ERROR",
                request
        );
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiErrorResponse> handleTimeoutException(TimeoutException ex, WebRequest request) {
        logger.error("Operation timeout for request {}", getRequestPath(request), ex);
        return buildErrorResponse(
                HttpStatus.REQUEST_TIMEOUT,
                "Request timeout. Please try again.",
                "TIMEOUT",
                request
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        logger.debug("No handler found for: {}", ex.getRequestURL());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Endpoint not found",
                "NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        logger.debug("No static resource found: {}", ex.getResourcePath());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "RESOURCE_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Invalid argument for request {}: {}", getRequestPath(request), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "INVALID_ARGUMENT",
                request
        );
    }

    @ExceptionHandler(com.talentboozt.s_backend.domains.community.exception.ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            com.talentboozt.s_backend.domains.community.exception.ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Resource not found for request {}: {}", getRequestPath(request), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/javascript")) {
            logger.warn("Runtime exception during non-JSON request {}: {}", getRequestPath(request), ex.getMessage());
            return null;
        }

        logger.error("Runtime exception occurred for request {}: {}", getRequestPath(request), ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred",
                "RUNTIME_ERROR",
                request
        );
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex,
            WebRequest request) {
        logger.warn("Could not write JSON response for request {}: {}", getRequestPath(request), ex.getMessage());
        return null;
    }

    @ExceptionHandler(io.github.resilience4j.ratelimiter.RequestNotPermitted.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitException(
            io.github.resilience4j.ratelimiter.RequestNotPermitted ex, WebRequest request) {
        logger.warn("Rate limit exceeded for request {}", getRequestPath(request));
        return buildErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded. Please try again later.",
                "RATE_LIMIT_EXCEEDED",
                request
        );
    }

    @ExceptionHandler(EduBaseException.class)
    public ResponseEntity<ApiErrorResponse> handleEduBaseException(EduBaseException ex, WebRequest request) {
        if (ex.getStatus().is4xxClientError()) {
            logger.warn("EDU domain client error for request {}: {} - {} - {}", getRequestPath(request), ex.getStatus(), ex.getErrorCode(), ex.getMessage());
        } else {
            logger.error("EDU domain server error for request {}: {} - {} - {}", getRequestPath(request), ex.getStatus(), ex.getErrorCode(), ex.getMessage());
        }

        return buildErrorResponse(
                (HttpStatus) ex.getStatus(),
                ex.getMessage(),
                ex.getErrorCode(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred for request {}", getRequestPath(request), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support if the problem persists.",
                "INTERNAL_ERROR",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String errorCode,
            WebRequest request) {
        return buildErrorResponse(status, message, errorCode, request, null);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String errorCode,
            WebRequest request,
            Object errors) {
        
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(getRequestPath(request))
                .errorCode(errorCode)
                .build();
        
        return new ResponseEntity<>(response, status);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "unknown";
    }
}
