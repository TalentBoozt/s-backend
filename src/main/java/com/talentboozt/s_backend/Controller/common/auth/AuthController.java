package com.talentboozt.s_backend.Controller.common.auth;

import com.talentboozt.s_backend.DTO.common.ErrorResponse;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Service.common.JwtService;
import com.talentboozt.s_backend.Service.common.KeyService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CredentialsService credentialsService;
    private final JwtService jwtService;
    private final KeyService keyService;

    public AuthController(CredentialsService credentialsService, JwtService jwtService, KeyService keyService) {
        this.credentialsService = credentialsService;
        this.jwtService = jwtService;
        this.keyService = keyService;
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

            try {
                // Decrypt stored password
                ResponseEntity<Map<String, String>> decryptedPassword = keyService.decryptData(user.getPassword());
                String password = decryptedPassword.getBody().get("data");

                // Compare decrypted password with input
                if (!password.equals(loginRequest.getPassword())) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Invalid password"));
                }

                // Generate JWT Token
                String token = jwtService.generateToken(user);

                return ResponseEntity.ok(new AuthResponse(token, user.getEmployeeId(), user.getEmail(), user.getUserLevel(), user.getOrganizations(), user.getPermissions(), user.getRoles(), user.isActive()));

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

                // Generate JWT Token for new user
                String token = jwtService.generateToken(newUser);

                return ResponseEntity.ok(new AuthResponse(token, newUser.getEmployeeId(), newUser.getEmail(), newUser.getUserLevel(), newUser.getOrganizations(), newUser.getPermissions(), newUser.getRoles(), newUser.isActive()));

            } catch (Exception e) {
                return ResponseEntity.status(500).body(new ErrorResponse("Encryption failed: " + e.getMessage()));
            }
        }

        // No username provided → cannot register
        return ResponseEntity.badRequest().body(new ErrorResponse("User not found! Please register first"));
    }
}

@Getter
@Setter
class AuthResponse {
    private String token;
    private String employeeId;
    private String email;
    private List<String> organizations;
    private List<String> permissions;
    private List<String> roles;
    private String userLevel;
    private boolean active;

    public AuthResponse(String token, String employeeId, String  email, String userLevel, List<String> organizations, List<String> permissions, List<String> roles, boolean active) {
        this.token = token;
        this.employeeId = employeeId;
        this.email = email;
        this.userLevel = userLevel;
        this.organizations = organizations;
        this.permissions = permissions;
        this.roles = roles;
        this.active = active;
    }
}
