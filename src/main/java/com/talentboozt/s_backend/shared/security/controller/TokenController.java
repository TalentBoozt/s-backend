package com.talentboozt.s_backend.shared.security.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import com.talentboozt.s_backend.shared.security.service.ValidateTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
public class TokenController {

    @Autowired
    private ValidateTokenService validateTokenService;

    @Autowired
    EmailService emailService;

    @GetMapping("/api/v2/validate-token")
    public ResponseEntity<ApiResponse> validateToken(@RequestParam String token) {
        if (validateTokenService.validateToken(token)) {
            return ResponseEntity.ok(new ApiResponse("Valid token"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Invalid token"));
        }
    }

    @PostMapping("/api/v2/send-access-token/{email}")
    public ResponseEntity<ApiResponse> sendAccessToken(@PathVariable String email) throws UnsupportedEncodingException {
        emailService.sendInterviewPreparationQuestionAccess(email);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/api/v2/send-notification-token/{email}")
    public ResponseEntity<ApiResponse> sendResetPassword(@PathVariable String email) throws UnsupportedEncodingException {
        emailService.sendNotificationToken(email);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }
}
