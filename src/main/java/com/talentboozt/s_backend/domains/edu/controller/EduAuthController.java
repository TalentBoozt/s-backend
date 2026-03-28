package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.auth.*;
import com.talentboozt.s_backend.domains.edu.service.EUserService;
import com.talentboozt.s_backend.shared.security.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/auth")
public class EduAuthController {

    private final EUserService userService;
    private final RateLimiterService rateLimiterService;

    public EduAuthController(EUserService userService, RateLimiterService rateLimiterService) {
        this.userService = userService;
        this.rateLimiterService = rateLimiterService;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest requestBody, HttpServletRequest request) {
        if (!rateLimiterService.checkRateLimit(getClientIp(request), "edu-register")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        return ResponseEntity.ok(userService.register(requestBody));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest requestBody, HttpServletRequest request) {
        if (!rateLimiterService.checkRateLimit(getClientIp(request), "edu-login")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        return ResponseEntity.ok(userService.login(requestBody));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        userService.verifyEmail(request.getToken());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest requestBody, HttpServletRequest request) {
        if (!rateLimiterService.checkRateLimit(getClientIp(request), "edu-forgot-password")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        userService.forgotPassword(requestBody.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset instructions sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request.getRefreshToken()));
    }
}
