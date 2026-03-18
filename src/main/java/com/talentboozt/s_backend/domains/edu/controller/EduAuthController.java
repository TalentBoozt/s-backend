package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.auth.AuthResponse;
import com.talentboozt.s_backend.domains.edu.dto.auth.LoginRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.RegisterRequest;
import com.talentboozt.s_backend.domains.edu.service.EUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/auth")
public class EduAuthController {

    private final EUserService userService;

    public EduAuthController(EUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
