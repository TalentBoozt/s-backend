package com.talentboozt.s_backend.Controller.common.auth;

import com.talentboozt.s_backend.DTO.common.ErrorResponse;
import com.talentboozt.s_backend.DTO.common.auth.SSO.JwtUserPayload;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Service.common.JwtService;
import com.talentboozt.s_backend.Service.common.KeyService;
import com.talentboozt.s_backend.Utils.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CredentialsService credentialsService;
    private final JwtService jwtService;
    private final KeyService keyService;
    private final JwtUtil jwtUtil;

    public AuthController(CredentialsService credentialsService, JwtService jwtService, KeyService keyService, JwtUtil jwtUtil) {
        this.credentialsService = credentialsService;
        this.jwtService = jwtService;
        this.keyService = keyService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping({"/login", "/login/{platform}"})
    public ResponseEntity<?> loginOrRegister(@PathVariable(value = "platform", required = false) String platform, @RequestBody CredentialsModel loginRequest) {
        Optional<CredentialsModel> userOptional = Optional.ofNullable(credentialsService.getCredentialsByEmail(loginRequest.getEmail()));

        // If user exists → LOGIN
        if (userOptional.isPresent()) {
            CredentialsModel user = userOptional.get();

            if (user.isDisabled()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Your account is disabled"));
            }

            JwtUserPayload userPayload = new JwtUserPayload();
            userPayload.setUserId(user.getEmployeeId());
            userPayload.setEmail(user.getEmail());
            userPayload.setUserLevel(user.getUserLevel());
            userPayload.setRoles(user.getRoles());
            userPayload.setPermissions(user.getPermissions());

            try {
                // Decrypt stored password
                ResponseEntity<Map<String, String>> decryptedPassword = keyService.decryptData(user.getPassword());
                String password = decryptedPassword.getBody().get("data");

                // Compare decrypted password with input
                if (!password.equals(loginRequest.getPassword())) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Invalid password"));
                }

                // Generate JWT Token
                String token = jwtService.generateToken(userPayload);
                String refreshToken = jwtService.generateRefreshToken(userPayload);

                return ResponseEntity.ok(new AuthResponse(token, refreshToken, user.getEmployeeId(), user.getEmail(), user.getUserLevel(), user.getOrganizations(), user.getPermissions(), user.getRoles(), user.isActive()));

            } catch (Exception e) {
                return ResponseEntity.status(500).body(new ErrorResponse("Decryption failed: " + e.getMessage()));
            }
        }

        // If user does NOT exist AND username is provided → REGISTER
        if (loginRequest.getFirstname() != null && !loginRequest.getFirstname().trim().isEmpty()) {
            try {
                ResponseEntity<Map<String, String>> encryptedPassword = keyService.encryptData(loginRequest.getPassword());
                String password = encryptedPassword.getBody().get("data");
                String referrer = loginRequest.getReferrerId();

                loginRequest.setPassword(password);

                CredentialsModel newUser = credentialsService.addCredentials(loginRequest, platform, referrer);

                if (newUser.isDisabled()) {
                    return ResponseEntity.status(403).body(new ErrorResponse("Registered user but your account is disabled"));
                }

                JwtUserPayload userPayload = new JwtUserPayload();
                userPayload.setUserId(newUser.getEmployeeId());
                userPayload.setEmail(newUser.getEmail());
                userPayload.setUserLevel(newUser.getUserLevel());
                userPayload.setRoles(newUser.getRoles());
                userPayload.setPermissions(newUser.getPermissions());

                // Generate JWT Token for new user
                String token = jwtService.generateToken(userPayload);
                String refreshToken = jwtService.generateRefreshToken(userPayload);

                return ResponseEntity.ok(new AuthResponse(token, refreshToken, newUser.getEmployeeId(), newUser.getEmail(), newUser.getUserLevel(), newUser.getOrganizations(), newUser.getPermissions(), newUser.getRoles(), newUser.isActive()));

            } catch (Exception e) {
                return ResponseEntity.status(500).body(new ErrorResponse("Encryption failed: " + e.getMessage()));
            }
        }

        // No username provided → cannot register
        return ResponseEntity.badRequest().body(new ErrorResponse("User not found! Please register first"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        // Validate and parse the refresh token
        if (jwtService.validateToken(refreshToken)) {
            // Extract user information from refresh token or database
            String email = jwtUtil.extractUsername(refreshToken);
            CredentialsModel user = credentialsService.getCredentialsByEmail(email);
            JwtUserPayload userPayload = new JwtUserPayload();
            userPayload.setUserId(user.getEmployeeId());
            userPayload.setEmail(user.getEmail());
            userPayload.setUserLevel(user.getUserLevel());
            userPayload.setRoles(user.getRoles());
            userPayload.setPermissions(user.getPermissions());

            // Generate new access token
            String newAccessToken = jwtService.generateToken(userPayload);
            String newRefreshToken = jwtService.generateRefreshToken(userPayload);

            // Return new access token
            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken, user.getEmployeeId(), user.getEmail(), user.getUserLevel(), user.getOrganizations(), user.getPermissions(), user.getRoles(), user.isActive()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid refresh token"));
    }

    @PostMapping("/social-login-process/{platform}")
    public ResponseEntity<?> socialLoginProcess(@RequestBody CredentialsModel credentials, @PathVariable String platform) {
        CredentialsModel savedUser = credentialsService.addCredentials(credentials, platform, credentials.getReferrerId());

        JwtUserPayload userPayload = new JwtUserPayload();
        userPayload.setUserId(savedUser.getEmployeeId());
        userPayload.setEmail(savedUser.getEmail());
        userPayload.setUserLevel(savedUser.getUserLevel());
        userPayload.setRoles(savedUser.getRoles());
        userPayload.setPermissions(savedUser.getPermissions());

        if (savedUser.isDisabled()) {
            return ResponseEntity.status(403).body(new ErrorResponse("Registered user but your account is disabled"));
        }

        // After successful save or fetch, generate tokens
        String accessToken = jwtService.generateToken(userPayload);
        String refreshToken = jwtService.generateRefreshToken(userPayload);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", savedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTokens/{email}")
    public ResponseEntity<?> getCredentialsByEmail(@PathVariable String email) {
        CredentialsModel credentials = credentialsService.getCredentialsByEmail(email);
        if (credentials == null) return null;

        JwtUserPayload userPayload = new JwtUserPayload();
        userPayload.setUserId(credentials.getEmployeeId());
        userPayload.setEmail(credentials.getEmail());
        userPayload.setUserLevel(credentials.getUserLevel());
        userPayload.setRoles(credentials.getRoles());
        userPayload.setPermissions(credentials.getPermissions());

        String accessToken = jwtService.generateToken(userPayload);
        String refreshToken = jwtService.generateRefreshToken(userPayload);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return ResponseEntity.ok(response);
    }
}

@Getter
@Setter
class AuthResponse {
    private String token;
    private String refreshToken;
    private String employeeId;
    private String email;
    private List<Map<String, String>> organizations;
    private List<String> permissions;
    private List<String> roles;
    private String userLevel;
    private boolean active;

    public AuthResponse(String token, String refreshToken, String employeeId, String  email, String userLevel, List<Map<String, String>> organizations, List<String> permissions, List<String> roles, boolean active) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.employeeId = employeeId;
        this.email = email;
        this.userLevel = userLevel;
        this.organizations = organizations;
        this.permissions = permissions;
        this.roles = roles;
        this.active = active;
    }
}

@Getter
@Setter
class RefreshTokenRequest {
    private String refreshToken;
}
