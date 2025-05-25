package com.talentboozt.s_backend.Controller.common.auth.SSO;

import com.talentboozt.s_backend.DTO.common.auth.SSO.*;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import com.talentboozt.s_backend.Repository.common.auth.CredentialsRepository;
import com.talentboozt.s_backend.Service.common.JwtService;
import com.talentboozt.s_backend.Service.common.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sso")
public class SsoAuthController {

    @Autowired
    private AuthService authService; // Your existing auth service logic (reuse)

    @Autowired
    private JwtService jwtService; // Service to create and validate JWTs

    @Autowired
    private CredentialsRepository credentialsRepository;

    private final int COOKIE_EXPIRATION = 60 * 60 * 24 * 7; // 7 days

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (authResponse == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid credentials"));
        }

        String token = jwtService.generateToken(authResponse.getUser());
        String refreshToken = jwtService.generateRefreshToken(authResponse.getUser());

        ResponseCookie cookie = ResponseCookie.from("TB_SESSION", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talentboozt.com")
                .path("/")
                .maxAge(COOKIE_EXPIRATION)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), token, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(registerRequest);

        if (authResponse == null) {
            return ResponseEntity.status(400).body(new MessageResponse("Registration failed"));
        }

        String token = jwtService.generateToken(authResponse.getUser());
        String refreshToken = jwtService.generateRefreshToken(authResponse.getUser());

        ResponseCookie cookie = ResponseCookie.from("TB_SESSION", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talentboozt.com")
                .path("/")
                .maxAge(COOKIE_EXPIRATION)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), token, refreshToken));
    }

    @GetMapping("/session")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {
        String token = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("TB_SESSION".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null || !jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(new MessageResponse("Session invalid"));
        }

        CredentialsModel user = jwtService.getUserFromToken(token);
        CredentialsModel existingUser = credentialsRepository.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Session invalid"));
        }
        SessionResponse session = new SessionResponse();
        session.setEmployeeId(user.getEmployeeId());
        session.setEmail(user.getEmail());
        session.setRoles(existingUser.getRoles());
        session.setPermissions(existingUser.getPermissions());
        session.setUserLevel(existingUser.getUserLevel());
        session.setCompanyId(existingUser.getCompanyId());
        session.setAccessedPlatforms(existingUser.getAccessedPlatforms());
        session.setOrganizations(existingUser.getOrganizations());

        return ResponseEntity.ok(session);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("TB_SESSION", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talentboozt.com")
                .path("/")
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader("Set-Cookie", expiredCookie.toString());

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
