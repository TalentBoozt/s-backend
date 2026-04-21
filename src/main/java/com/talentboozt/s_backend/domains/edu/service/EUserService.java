package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.auth.LoginRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.RegisterRequest;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.edu.dto.auth.AuthResponse;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduInvalidCredentialsException;
import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EduJwtService jwtService;
    private final EduSubscriptionService subscriptionService;
    private final CredentialsService credentialsService;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:https://edu.talnova.io}")
    private String frontendUrl;

    public EUserService(EUserRepository userRepository, EProfilesRepository profileRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder, EduJwtService jwtService,
            EduSubscriptionService subscriptionService,
            CredentialsService credentialsService,
            JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.subscriptionService = subscriptionService;
        this.credentialsService = credentialsService;
        this.mailSender = mailSender;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Set<ERoles> roles = new HashSet<>();
        roles.add(ERoles.LEARNER);

        String requestedRole = request.getRole() != null ? request.getRole().trim().toUpperCase() : "LEARNER";
        if (requestedRole.contains("INSTRUCTOR")) {
            roles.add(ERoles.ENTERPRISE_INSTRUCTOR);
        } else if (requestedRole.contains("CREATOR") || requestedRole.contains("SELLER")) {
            roles.add(ERoles.SELLER_FREE);
        } else if (requestedRole.contains("PLATFORM_ADMIN")) {
            roles.add(ERoles.PLATFORM_ADMIN);
        } else if (requestedRole.contains("ADMIN")) {
            roles.add(ERoles.ENTERPRISE_ADMIN);
        } else if (requestedRole.contains("ENTERPRISE_LEARNER")) {
            roles.add(ERoles.ENTERPRISE_LEARNER);
        } else if (requestedRole.contains("MANAGER")) {
            roles.add(ERoles.ENTERPRISE_MANAGER);
        } else if (requestedRole.contains("REVIEWER")) {
            roles.add(ERoles.REVIEWER);
        }

        // Core Ecosystem Orchestration: Ensuring a global SSO identity exists
        com.talentboozt.s_backend.domains.auth.model.CredentialsModel globalCreds = com.talentboozt.s_backend.domains.auth.model.CredentialsModel
                .builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .roles(roles.stream().map(Enum::name).collect(java.util.stream.Collectors.toList()))
                .platformRole("USER")
                .registeredFrom("EDU_PLATFORM")
                .build();

        // This will find existing user or create a new one across all domains
        globalCreds = credentialsService.addCredentials(globalCreds, "EDU_PLATFORM", null);
        String userIdToUse = globalCreds.getEmployeeId();

        // Check if EDU-specific profile already exists for this unique platform ID
        if (userRepository.findById(userIdToUse).isPresent()) {
            throw new EduBadRequestException("Profile already exists on this platform for this email. Please login.");
        }

        String verificationToken = UUID.randomUUID().toString();

        EUser newUser = EUser.builder()
                .id(userIdToUse)
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
        Optional<EUser> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            EUser user = userOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new EduInvalidCredentialsException("Invalid credentials");
            }
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            return buildAuthResponse(user);
        }

        // Fallback: Check if user exists in the core ecosystem (SSO)
        com.talentboozt.s_backend.domains.auth.model.CredentialsModel globalCreds = credentialsService
                .getCredentialsByEmail(request.getEmail());

        if (globalCreds != null) {
            // Verify global password (which is also encrypted with the same utility)
            if (passwordEncoder.matches(request.getPassword(), globalCreds.getPassword())) {
                // Auto-provision EDU profile linked to this global identity
                EUser provisionedUser = EUser.builder()
                        .id(globalCreds.getEmployeeId())
                        .email(globalCreds.getEmail())
                        .passwordHash(globalCreds.getPassword())
                        .displayName(globalCreds.getFirstname() + " " + globalCreds.getLastname())
                        .roles(new ERoles[] { ERoles.LEARNER }) // Default role
                        .isActive(true)
                        .isEmailVerified(true) // Already verified on platform
                        .lastLoginAt(Instant.now())
                        .build();

                EUser saved = userRepository.save(provisionedUser);

                // Also create basic profile
                EProfiles newProfile = EProfiles.builder()
                        .userId(saved.getId())
                        .firstName(globalCreds.getFirstname())
                        .lastName(globalCreds.getLastname())
                        .publicEmail(globalCreds.getEmail())
                        .build();
                profileRepository.save(newProfile);
                subscriptionService.assignDefaultFreePlan(saved.getId());

                return buildAuthResponse(saved);
            }
        }

        throw new EduInvalidCredentialsException("Invalid credentials");
    }

    public void verifyEmail(String token) {
        EUser user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new EduBadRequestException("Invalid verification token"));

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
                .orElseThrow(() -> new EduBadRequestException("Invalid reset token"));

        if (user.getPasswordResetExpiry().isBefore(Instant.now())) {
            throw new EduBadRequestException("Reset token expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "No refresh token provided");
        }

        if (!jwtService.validateToken(refreshToken)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "User identity lost. Please login again."));

        return buildAuthResponse(user);
    }

    public AuthResponse getMe(String userId) {
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "User not found"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(EUser user) {
        com.talentboozt.s_backend.domains.edu.model.ESubscriptions sub = subscriptionService
                .getUserSubscription(user.getId());
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(user)
                .currentPlan(sub != null ? sub.getPlan()
                        : com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.FREE)
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
