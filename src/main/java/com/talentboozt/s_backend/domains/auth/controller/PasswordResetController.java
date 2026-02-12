package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.domains.auth.dto.PasswordResetRequest;
import com.talentboozt.s_backend.domains.auth.dto.PasswordResetRequestDTO;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.model.PasswordResetTokenModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.PasswordResetTokenRepository;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.auth.service.PasswordResetService;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v2/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {
        try {
            String token = passwordResetService.createPasswordResetToken(request.getEmail());
            emailService.sendPasswordResetEmail(request.getEmail(), token);
            return ResponseEntity.ok(new ApiResponse("Reset link sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Email not found."));
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        Optional<PasswordResetTokenModel> optionalToken = passwordResetTokenRepository.findByToken(request.getToken());
        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid token."));
        } else {
            PasswordResetTokenModel token = optionalToken.get();
            System.out.println(token.getUserId());
            System.out.println(request.getNewPassword());

            CredentialsModel credentials = credentialsService.updatePassword(token.getUserId(), request.getNewPassword());
            if (credentials == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid token."));
            }
            passwordResetTokenRepository.delete(token);
            return ResponseEntity.ok(new ApiResponse("Password reset successful."));
        }
    }
}