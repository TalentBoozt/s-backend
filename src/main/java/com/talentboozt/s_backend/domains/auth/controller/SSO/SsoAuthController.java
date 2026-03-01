package com.talentboozt.s_backend.domains.auth.controller.SSO;

import com.talentboozt.s_backend.domains.auth.dto.SSO.*;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.domains.auth.service.UserPermissionsService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import com.talentboozt.s_backend.domains.auth.service.AuthService;
import com.talentboozt.s_backend.shared.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sso")
public class SsoAuthController {

    private final AuthService authService; // existing auth service logic (reuse)
    private final JwtService jwtService; // Service to create and validate JWTs
    private final CredentialsRepository credentialsRepository;
    private final JwtUtil jwtUtil;
    private final UserPermissionsService userPermissionsService;
    private final com.talentboozt.s_backend.domains.payment.service.SubscriptionService subscriptionService;

    public SsoAuthController(AuthService authService, JwtService jwtService,
            CredentialsRepository credentialsRepository,
            JwtUtil jwtUtil, UserPermissionsService userPermissionsService,
            com.talentboozt.s_backend.domains.payment.service.SubscriptionService subscriptionService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.credentialsRepository = credentialsRepository;
        this.jwtUtil = jwtUtil;
        this.userPermissionsService = userPermissionsService;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (authResponse == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid credentials"));
        }

        String accessToken = jwtService.generateToken(authResponse.getUser());
        String refreshToken = jwtService.generateRefreshToken(authResponse.getUser());

        ResponseCookie accessCookie = ResponseCookie.from("TB_SESSION", Objects.requireNonNull(accessToken))
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(3600) // 1 hour
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("TB_REFRESH", Objects.requireNonNull(refreshToken))
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(2592000) // 30 days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(registerRequest);

        if (authResponse == null) {
            return ResponseEntity.status(400).body(new MessageResponse("Registration failed"));
        }

        String accessToken = jwtService.generateToken(authResponse.getUser());
        String refreshToken = jwtService.generateRefreshToken(authResponse.getUser());

        ResponseCookie accessCookie = ResponseCookie.from("TB_SESSION", Objects.requireNonNull(accessToken))
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(3600) // 1 hour
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("TB_REFRESH", Objects.requireNonNull(refreshToken))
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(2592000) // 30 days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), accessToken, refreshToken));
    }

    @GetMapping("/session")
    public ResponseEntity<?> validateSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("TB_SESSION".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("TB_REFRESH".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        CredentialsModel user = null;

        // 1. Try validating access token first
        if (accessToken != null && jwtService.validateToken(accessToken)) {
            user = jwtService.getUserFromToken(accessToken);
        }

        // 2. If access token is invalid or missing, try refreshing with refresh token
        if (user == null && refreshToken != null && jwtService.validateToken(refreshToken)) {
            String email = jwtUtil.extractUsername(refreshToken);
            CredentialsModel dbUser = credentialsRepository.findByEmail(email);

            if (dbUser != null) {
                JwtUserPayload payload = new JwtUserPayload();
                payload.setUserId(dbUser.getEmployeeId());
                payload.setEmail(dbUser.getEmail());
                payload.setUserLevel(dbUser.getUserLevel());
                payload.setRoles(dbUser.getRoles());
                payload.setPermissions(dbUser.getPermissions());
                payload.setOrganizations(dbUser.getOrganizations());

                // Rotate tokens
                String newAccessToken = jwtService.generateToken(payload);
                String newRefreshToken = jwtService.generateRefreshToken(payload);

                // Set new cookies
                ResponseCookie accessCookie = ResponseCookie.from("TB_SESSION", Objects.requireNonNull(newAccessToken))
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .domain(".talnova.io")
                        .path("/")
                        .maxAge(3600) // 1 hour
                        .build();

                ResponseCookie refreshCookie = ResponseCookie
                        .from("TB_REFRESH", Objects.requireNonNull(newRefreshToken))
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .domain(".talnova.io")
                        .path("/")
                        .maxAge(2592000) // 30 days
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                user = dbUser; // now session can continue
                accessToken = newAccessToken;
            }
        }

        // 3. Still no user? Then session is invalid
        if (user == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Session invalid"));
        }

        // 4. Return session info
        CredentialsModel existingUser = credentialsRepository.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Session invalid"));
        }

        SessionResponse session = new SessionResponse();
        session.setEmployeeId(user.getEmployeeId());
        session.setEmail(user.getEmail());
        session.setRoles(existingUser.getRoles());

        boolean isExempt = subscriptionService.isExempt(existingUser.getCompanyId());
        session.setPermissions(
                userPermissionsService.resolvePermissionsWithSystemBypass(existingUser.getRoles(), isExempt));

        session.setUserLevel(existingUser.getUserLevel());
        session.setCompanyId(existingUser.getCompanyId());
        session.setAccessedPlatforms(existingUser.getAccessedPlatforms());
        session.setOrganizations(existingUser.getOrganizations());

        return ResponseEntity.ok(session);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie expiredAccess = ResponseCookie.from("TB_SESSION", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie expiredRefresh = ResponseCookie.from("TB_REFRESH", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talnova.io")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefresh.toString());

        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}

@Getter
@Setter
class SessionResponse {
    private String employeeId;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private String userLevel;
    private String companyId;
    private List<String> accessedPlatforms;
    private List<Map<String, String>> organizations;
}
