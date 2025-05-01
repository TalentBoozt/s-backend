package com.talentboozt.s_backend.Controller.common.auth.SSO;

import com.talentboozt.s_backend.DTO.common.auth.SSO.*;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.JwtService;
import com.talentboozt.s_backend.Service.common.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sso")
public class SsoAuthController {

    @Autowired
    private AuthService authService; // Your existing auth service logic (reuse)

    @Autowired
    private JwtService jwtService; // Service to create and validate JWTs

    private final int COOKIE_EXPIRATION = 60 * 60 * 24 * 7; // 7 days

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (authResponse == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid credentials"));
        }

        String token = jwtService.generateToken(authResponse.getUser());

        ResponseCookie cookie = ResponseCookie.from("TB_SESSION", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talentboozt.com")
                .path("/")
                .maxAge(COOKIE_EXPIRATION)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(registerRequest);

        if (authResponse == null) {
            return ResponseEntity.status(400).body(new MessageResponse("Registration failed"));
        }

        String token = jwtService.generateToken(authResponse.getUser());

        ResponseCookie cookie = ResponseCookie.from("TB_SESSION", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".talentboozt.com")
                .path("/")
                .maxAge(COOKIE_EXPIRATION)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new RedirectResponse(authResponse.getRedirectUri(), token));
    }

    @GetMapping("/session")
    public ResponseEntity<?> validateSession(@CookieValue(name = "TB_SESSION", required = false) String token) {
        if (token == null || !jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(new MessageResponse("Session invalid"));
        }

        CredentialsModel user = jwtService.getUserFromToken(token);
        return ResponseEntity.ok(user);
    }
}
