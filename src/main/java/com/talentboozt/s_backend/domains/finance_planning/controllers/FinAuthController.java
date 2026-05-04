package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinAuthResponse;
import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinLoginRequest;
import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinRegisterRequest;
import com.talentboozt.s_backend.domains.finance_planning.services.FinJwtService;
import com.talentboozt.s_backend.domains.finance_planning.services.FinUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/auth")
public class FinAuthController {

    private final FinUserService userService;
    private final FinJwtService jwtService;

    public FinAuthController(FinUserService userService, FinJwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<FinAuthResponse> register(@RequestBody FinRegisterRequest request, HttpServletResponse response) {
        FinAuthResponse authResponse = userService.register(request);
        setCookies(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<FinAuthResponse> login(@RequestBody FinLoginRequest request, HttpServletResponse response) {
        FinAuthResponse authResponse = userService.login(request);
        setCookies(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie accessCookie = jwtService.getCleanAccessTokenCookie();
        ResponseCookie refreshCookie = jwtService.getCleanRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<FinAuthResponse> getMe(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        FinAuthResponse response = userService.getMe(token);
        return ResponseEntity.ok(response);
    }

    private void setCookies(FinAuthResponse authResponse, HttpServletResponse response) {
        ResponseCookie accessCookie = jwtService.generateAccessTokenCookie(authResponse.getUser());
        ResponseCookie refreshCookie = jwtService.generateRefreshTokenCookie(authResponse.getUser());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
