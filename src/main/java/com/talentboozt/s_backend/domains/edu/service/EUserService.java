package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.auth.LoginRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.RegisterRequest;
import com.talentboozt.s_backend.domains.edu.dto.auth.AuthResponse;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
public class EUserService {

    private final EUserRepository userRepository;
    private final EProfilesRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EduJwtService jwtService;
    private final EduSubscriptionService subscriptionService;

    public EUserService(EUserRepository userRepository, EProfilesRepository profileRepository,
            PasswordEncoder passwordEncoder, EduJwtService jwtService,
            EduSubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.subscriptionService = subscriptionService;
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
        } else if ("MANAGER".equals(request.getRole())) {
            roles.add(ERoles.MANAGER);
        } else if ("EMPLOYEE".equals(request.getRole())) {
            roles.add(ERoles.EMPLOYEE);
        }

        EUser newUser = EUser.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getFirstName() + " " + request.getLastName())
                .phone(request.getPhone())
                .roles(roles.toArray(new ERoles[0]))
                .isActive(true)
                .isEmailVerified(false)
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

    private AuthResponse buildAuthResponse(EUser user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(user)
                .build();
    }
}
