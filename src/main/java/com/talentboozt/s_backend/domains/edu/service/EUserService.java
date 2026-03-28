package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.auth.LoginRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.RegisterRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.AuthResponse;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class EUserService {

    private final EUserRepository userRepository;
    private final EProfilesRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EduJwtService jwtService;
    private final EduSubscriptionService subscriptionService;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public EUserService(EUserRepository userRepository, EProfilesRepository profileRepository,
            PasswordEncoder passwordEncoder, EduJwtService jwtService,
            EduSubscriptionService subscriptionService,
            JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.subscriptionService = subscriptionService;
        this.mailSender = mailSender;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Set<ERoles> roles = new HashSet<>();
        roles.add(ERoles.LEARNER);

        if ("INSTRUCTOR".equals(request.getRole())) {
            roles.add(ERoles.INSTRUCTOR);
        } else if ("CREATOR".equals(request.getRole())) {
            roles.add(ERoles.CREATOR);
        } else if ("ADMIN".equals(request.getRole())) {
            roles.add(ERoles.ADMIN);
        }

        String verificationToken = UUID.randomUUID().toString();

        EUser newUser = EUser.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getFirstName() + " " + request.getLastName())
                .phone(request.getPhone())
                .roles(roles.toArray(new ERoles[0]))
                .isActive(true)
                .isEmailVerified(false)
                .emailVerificationToken(verificationToken)
                .build();

        EUser savedUser = userRepository.save(newUser);

        EProfiles newProfile = EProfiles.builder()
                .userId(savedUser.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .publicEmail(request.getEmail())
                .build();

        profileRepository.save(newProfile);
        subscriptionService.assignDefaultFreePlan(savedUser.getId());

        sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        EUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public void verifyEmail(String token) {
        EUser user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        Optional<EUser> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            EUser user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetExpiry(Instant.now().plus(30, ChronoUnit.MINUTES));
            userRepository.save(user);
            sendPasswordResetEmail(user.getEmail(), token);
        }
        // Always return success to avoid email enumeration
    }

    public void resetPassword(String token, String newPassword) {
        EUser user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getPasswordResetExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(EUser user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(user)
                .build();
    }

    private void sendVerificationEmail(String to, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        String content = "<h1>Verify your email</h1><p>Please click the link below to verify your email:</p>"
                + "<a href=\"" + link + "\">Verify Email</a>";
        sendEmail(to, "Verify your email", content);
    }

    private void sendPasswordResetEmail(String to, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        String content = "<h1>Reset your password</h1><p>Please click the link below to reset your password:</p>"
                + "<a href=\"" + link + "\">Reset Password</a>";
        sendEmail(to, "Reset your password", content);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't crash
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}
