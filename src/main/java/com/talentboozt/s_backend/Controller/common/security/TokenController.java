package com.talentboozt.s_backend.Controller.common.security;

import com.talentboozt.s_backend.DTO.common.ApiResponse;
import com.talentboozt.s_backend.Service.common.mail.EmailService;
import com.talentboozt.s_backend.Service.common.security.ValidateTokenService;
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
        System.out.println("Token: " + token);
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
