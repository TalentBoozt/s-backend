package com.talentboozt.s_backend.Controller.common.auth;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Service.common.JwtService;
import com.talentboozt.s_backend.Service.common.KeyService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                return ResponseEntity.badRequest().body("Your account is disabled");
            }

            try {
                // Decrypt stored password
                String decryptedPassword = keyService.decryptData(user.getPassword()).getBody().get("data");

                // Compare decrypted password with input
                if (!decryptedPassword.equals(loginRequest.getPassword())) {
                    return ResponseEntity.badRequest().body("Invalid password");
                }

                // Generate JWT Token
                String token = jwtService.generateToken(user);

                return ResponseEntity.ok(new AuthResponse(token, user.getEmployeeId(), user.getUserLevel()));

            } catch (Exception e) {
                return ResponseEntity.status(500).body("Decryption failed: " + e.getMessage());
            }
        }

        // If user does NOT exist AND username is provided → REGISTER
        if (loginRequest.getFirstname() != null && !loginRequest.getFirstname().trim().isEmpty()) {
            try {
                String encryptedPassword = keyService.encryptData(loginRequest.getPassword()).getBody().get("data");
                String referrer = loginRequest.getReferrerId();

                loginRequest.setPassword(encryptedPassword);

                CredentialsModel newUser = credentialsService.addCredentials(loginRequest, platform, referrer);

                if (newUser.isDisabled()) {
                    return ResponseEntity.badRequest().body("Registered user but your account is disabled");
                }

                // Generate JWT Token for new user
                String token = jwtService.generateToken(newUser);

                return ResponseEntity.ok(new AuthResponse(token, newUser.getEmployeeId(), newUser.getUserLevel()));

            } catch (Exception e) {
                return ResponseEntity.status(500).body("Encryption failed: " + e.getMessage());
            }
        }

        // No username provided → cannot register
        return ResponseEntity.badRequest().body("User not found and no registration data provided.");
    }
}

@Getter
@Setter
class AuthResponse {
    private String token;
    private String employeeId;
    private String userLevel;

    public AuthResponse(String token, String employeeId, String userLevel) {
        this.token = token;
        this.employeeId = employeeId;
        this.userLevel = userLevel;
    }
}
