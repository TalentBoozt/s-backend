package com.talentboozt.s_backend.Controller.common.auth;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Utils.ConfigUtility;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/api/v2/oauth")
public class GoogleOAuthController {


    private final ConfigUtility configUtil;
    private final RestTemplate restTemplate;
    private final CredentialsService credentialsService;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String REDIRECT_URI = "http://localhost:4200/api/v2/oauth/callback/google";

    public GoogleOAuthController(ConfigUtility configUtil, RestTemplate restTemplate, CredentialsService credentialsService) {
        this.configUtil = configUtil;
        this.restTemplate = restTemplate;
        this.credentialsService = credentialsService;
    }

    // Step 1: Handle OAuth callback
    @PostMapping("/callback/google")
    public ResponseEntity<?> handleGoogleCallback(@RequestBody Map<String, String> requestBody) throws NoSuchAlgorithmException {
        String authorizationCode = requestBody.get("code");
        String codeVerifier = UUID.randomUUID().toString().replace("-", "");
        String codeChallenge = Base64.getUrlEncoder().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(codeVerifier.getBytes())
        );

        if (authorizationCode == null) {
            return ResponseEntity.badRequest().body("Authorization code is missing");
        }

        // Step 2: Exchange authorization code for access token
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("code", authorizationCode);
        tokenRequest.put("code_verifier", codeVerifier);
        tokenRequest.put("code_challenge", codeChallenge);
        tokenRequest.put("client_id", configUtil.getProperty("GOOGLE_CLIENT_ID"));
        tokenRequest.put("client_secret", configUtil.getProperty("GOOGLE_CLIENT_SECRET"));
        tokenRequest.put("redirect_uri", REDIRECT_URI);
        tokenRequest.put("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(GOOGLE_TOKEN_URL, tokenRequest, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to exchange token");
        } else if (tokenResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization code");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Step 3: Fetch user info
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(GOOGLE_USERINFO_URL, HttpMethod.GET, entity, Map.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch user info");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();

        // Step 4: Register or login the user
        String email = (String) userInfo.get("email");
        String firstName = (String) userInfo.get("given_name");
        String lastName = (String) userInfo.get("family_name");

        Optional<CredentialsModel> OptionalExistingUser = Optional.ofNullable(credentialsService.getCredentialsByEmail(email));
        if (OptionalExistingUser.isEmpty()) {
            CredentialsModel newUser = new CredentialsModel();
            newUser.setEmail(email);
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setRole("candidate");
            newUser.setUserLevel("1");
            CredentialsModel savedUser = credentialsService.addCredentials(newUser, null, null);
            return ResponseEntity.ok(savedUser);
        } else {
            CredentialsModel existingUser = OptionalExistingUser.get();
            return ResponseEntity.ok(existingUser);
        }
    }
}

