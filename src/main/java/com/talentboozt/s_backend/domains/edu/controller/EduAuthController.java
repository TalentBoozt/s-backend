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

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import com.talentboozt.s_backend.domains.edu.service.EduJwtService;

@RestController
@RequestMapping("/api/edu/auth")
public class EduAuthController {

    private final EUserService userService;
    private final RateLimiterService rateLimiterService;
    private final EduJwtService jwtService;

    public EduAuthController(EUserService userService, RateLimiterService rateLimiterService, EduJwtService jwtService) {
        this.userService = userService;
        this.rateLimiterService = rateLimiterService;
        this.jwtService = jwtService;
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
        AuthResponse response = userService.register(requestBody);
        
        ResponseCookie cookie = jwtService.generateAccessTokenCookie(response.getUser());
        ResponseCookie refreshCookie = jwtService.generateRefreshTokenCookie(response.getUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest requestBody, HttpServletRequest request) {
        if (!rateLimiterService.checkRateLimit(getClientIp(request), "edu-login")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        AuthResponse response = userService.login(requestBody);
        
        ResponseCookie cookie = jwtService.generateAccessTokenCookie(response.getUser());
        ResponseCookie refreshCookie = jwtService.generateRefreshTokenCookie(response.getUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
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
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody(required = false) TokenRefreshRequest body, jakarta.servlet.http.HttpServletRequest request) {
        String token = (body != null) ? body.getRefreshToken() : null;
        
        if (token == null && request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("edu_refresh_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        
        AuthResponse response = userService.refreshToken(token);
        ResponseCookie cookie = jwtService.generateAccessTokenCookie(response.getUser());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cleanCookie = jwtService.getCleanAccessTokenCookie();
        ResponseCookie cleanRefreshCookie = jwtService.getCleanRefreshTokenCookie();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                .build();
    }
}
