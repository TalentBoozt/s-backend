package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.DTO.common.auth.SSO.AuthResponse;
import com.talentboozt.s_backend.DTO.common.auth.SSO.RegisterRequest;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add JWTService if you need to generate token inside service level (for flexibility)

    public AuthResponse login(String email, String password) {
        CredentialsModel user = credentialsService.getCredentialsByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // Determine redirect URL based on user or platform type if needed
        String redirectUri = determineRedirectUri(user);

        return new AuthResponse(user, redirectUri);
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String platform = registerRequest.getPlatform();
        String referrer = registerRequest.getReferrerId();

        // Check if user already exists
        if (credentialsService.isExistsByEmail(registerRequest.getEmail())) {
            return null;
        }

        CredentialsModel user = new CredentialsModel();
        user.setFirstname(registerRequest.getFirstname());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setUserLevel(registerRequest.getUserLevel());
        // add other fields if you have

        CredentialsModel savedUser = credentialsService.addCredentials(user, platform, referrer);

        // Determine where to redirect after successful registration
        String redirectUri = determineRedirectUri(savedUser);

        return new AuthResponse(savedUser, redirectUri);
    }

    private String determineRedirectUri(CredentialsModel user) {
        // Simple example
        // You can modify this logic based on User Role, Platform Field, or any incoming Query Param
        String defaultUri = "https://app.talentboozt.com";

        if (user.getRole().equalsIgnoreCase("candidate")) {
            return "https://cv.talentboozt.com/dashboard";
        } else if (user.getRole().equalsIgnoreCase("employer")) {
            return "https://jobboard.talentboozt.com/dashboard";
        } else {
            return defaultUri;
        }
    }
}

