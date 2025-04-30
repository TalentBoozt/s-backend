package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.DTO.common.auth.SSO.AuthResponse;
import com.talentboozt.s_backend.DTO.common.auth.SSO.RegisterRequest;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Repository.common.auth.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private CredentialsRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add JWTService if you need to generate token inside service level (for flexibility)

    public AuthResponse login(String email, String password) {
        CredentialsModel user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // Determine redirect URL based on user or platform type if needed
        String redirectUri = determineRedirectUri(user);

        return new AuthResponse(user, redirectUri);
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
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

        userRepository.save(user);

        // Determine where to redirect after successful registration
        String redirectUri = determineRedirectUri(user);

        return new AuthResponse(user, redirectUri);
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

