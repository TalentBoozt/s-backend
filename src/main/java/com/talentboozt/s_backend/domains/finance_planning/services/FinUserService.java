package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinAuthResponse;
import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinLoginRequest;
import com.talentboozt.s_backend.domains.finance_planning.dtos.auth.FinRegisterRequest;
import com.talentboozt.s_backend.domains.finance_planning.models.FinUser;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinUserRepository;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class FinUserService {

    private final FinUserRepository finUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final FinJwtService jwtService;
    private final CredentialsService credentialsService;

    public FinUserService(FinUserRepository finUserRepository, 
                         PasswordEncoder passwordEncoder, 
                         FinJwtService jwtService,
                         CredentialsService credentialsService) {
        this.finUserRepository = finUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.credentialsService = credentialsService;
    }

    @Transactional
    public FinAuthResponse register(FinRegisterRequest request) {
        // Global SSO check/creation
        CredentialsModel globalCreds = CredentialsModel.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .platformRole("USER")
                .registeredFrom("FINANCE_PLATFORM")
                .build();

        globalCreds = credentialsService.addCredentials(globalCreds, "FINANCE_PLATFORM", null);
        String userIdToUse = globalCreds.getEmployeeId();

        if (finUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Profile already exists. Please login.");
        }

        FinUser newUser = FinUser.builder()
                .id(userIdToUse)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getFirstName() + " " + request.getLastName())
                .isActive(true)
                .build();

        FinUser savedUser = finUserRepository.save(newUser);
        return buildAuthResponse(savedUser);
    }

    public FinAuthResponse login(FinLoginRequest request) {
        Optional<FinUser> userOpt = finUserRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            FinUser user = userOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
            }
            user.setLastLoginAt(Instant.now());
            finUserRepository.save(user);
            return buildAuthResponse(user);
        }

        // Fallback: SSO check
        CredentialsModel globalCreds = credentialsService.getCredentialsByEmail(request.getEmail());
        if (globalCreds != null) {
            if (passwordEncoder.matches(request.getPassword(), globalCreds.getPassword())) {
                // Auto-provision
                FinUser provisionedUser = FinUser.builder()
                        .id(globalCreds.getEmployeeId())
                        .email(globalCreds.getEmail())
                        .passwordHash(globalCreds.getPassword())
                        .displayName(globalCreds.getFirstname() + " " + globalCreds.getLastname())
                        .roles(globalCreds.getRoles() != null ? globalCreds.getRoles().toArray(new String[0]) : null)
                        .organizations(globalCreds.getOrganizations())
                        .activeWorkspaceId(globalCreds.getActiveWorkspaceId())
                        .isActive(true)
                        .lastLoginAt(Instant.now())
                        .build();

                FinUser saved = finUserRepository.save(provisionedUser);
                return buildAuthResponse(saved);
            }
        }

        throw new RuntimeException("Invalid credentials");
    }

    private FinAuthResponse buildAuthResponse(FinUser user) {
        return FinAuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(user)
                .build();
    }

    public FinAuthResponse getMe(String token) {
        String userId = jwtService.extractUserId(token);
        FinUser user = finUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildAuthResponse(user);
    }
}
