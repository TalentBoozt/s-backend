package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.dto.SSO.AuthResponse;
import com.talentboozt.s_backend.domains.auth.dto.SSO.JwtUserPayload;
import com.talentboozt.s_backend.domains.auth.dto.SSO.RegisterRequest;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    // Add JWTService if you need to generate token inside service level (for flexibility)

    public AuthResponse login(String email, String password) {
        CredentialsModel user = credentialsService.getCredentialsByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        JwtUserPayload userPayload = new JwtUserPayload();
        userPayload.setUserId(user.getEmployeeId());
        userPayload.setEmail(user.getEmail());
        userPayload.setUserLevel(user.getUserLevel());
        userPayload.setRoles(user.getRoles());
        userPayload.setPermissions(userPermissionsService.resolvePermissions(user.getRoles()));

        // Determine redirect URL based on user or platform type if needed
        String redirectUri = determineRedirectUri(user);

        return new AuthResponse(userPayload, redirectUri);
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String platform = registerRequest.getPlatform();
        String referrer = registerRequest.getReferrerId();
        String def_role = Optional.ofNullable(registerRequest.getRole()).orElse("END_USER");

        // Check if user already exists
        if (credentialsService.isExistsByEmail(registerRequest.getEmail())) {
            return null;
        }

        Set<String> userRoles = new HashSet<>();
        userRoles.add(def_role);

        Set<String> userPermissions = new HashSet<>();
        for (String roleName : userRoles) {
            Optional<RoleModel> role = roleService.getRoleByName(roleName);
            if (role.isPresent()){
                RoleModel optRole = role.get();
                if (optRole.getPermissions() != null) {
                    userPermissions.addAll(optRole.getPermissions());
                }
            }
            if (role.isEmpty()) {
                RoleModel newRole = new RoleModel();
                newRole.setName(roleName);
                newRole.setPermissions(new ArrayList<>());
                newRole.setDescription("Default role for " + roleName);
                RoleModel savedRole = roleService.addRole(newRole);

                userRoles.add(savedRole.getName());
                userPermissions.addAll(savedRole.getPermissions());
            }
        }

        CredentialsModel user = new CredentialsModel();
        user.setFirstname(registerRequest.getFirstname());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(def_role);
        user.setRoles(new ArrayList<>(userRoles));
        user.setPermissions(new ArrayList<>(userPermissions));
        user.setUserLevel(registerRequest.getUserLevel());
        // add other fields if you have

        CredentialsModel savedUser = credentialsService.addCredentials(user, platform, referrer);
        JwtUserPayload userPayload = new JwtUserPayload();
        userPayload.setUserId(savedUser.getEmployeeId());
        userPayload.setEmail(savedUser.getEmail());
        userPayload.setUserLevel(savedUser.getUserLevel());
        userPayload.setRoles(savedUser.getRoles());
        userPayload.setPermissions(savedUser.getPermissions());

        // Determine where to redirect after successful registration
        String redirectUri = determineRedirectUri(savedUser);

        return new AuthResponse(userPayload, redirectUri);
    }

    private String determineRedirectUri(CredentialsModel user) {
        String defaultUri = "https://talentboozt.com";

        if (user.getRole().equalsIgnoreCase("candidate")) {
            return "https://talentboozt.com/candidate-profile";
        } else if (user.getRole().equalsIgnoreCase("employer")) {
            return "https://talentboozt.com/dashboard";
        } else {
            return defaultUri;
        }
    }
}

