package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.auth.dto.LinkedInAuthRequest;
import com.talentboozt.s_backend.domains.auth.dto.LinkedInTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/linkedin")
public class LinkedinAuthController {
    @Value("${linkedin.client-id}")
    private String clientId;

    @Value("${linkedin.client-secret}")
    private String clientSecret;

    @Value("${linkedin.redirect-uri}")
    private String redirectUri;

    @Value("${linkedin.token-url}")
    private String tokenUrl;

    @Value("${linkedin.profile-url}")
    private String profileUrl;

    @Value("${linkedin.email-url}")
    private String emailUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/exchange-code")
    public ResponseEntity<?> exchangeCode(@RequestBody LinkedInAuthRequest request) {
        // Build the access token request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", request.getCode());
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<LinkedInTokenResponse> response = restTemplate.postForEntity(tokenUrl, httpRequest, LinkedInTokenResponse.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to exchange authorization code: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getLinkedInProfile(@RequestParam("accessToken") String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

//        try {
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch profile: " + e.getMessage());
//        }

        // Fetch basic profile
        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> profileResponse = restTemplate.exchange(profileUrl, HttpMethod.GET, httpRequest, Map.class);

        // Fetch email address
//        ResponseEntity<Map> emailResponse = restTemplate.exchange(emailUrl, HttpMethod.GET, httpRequest, Map.class);

        Map<String, Object> result = new HashMap<>();
        result.put("profile", profileResponse.getBody());
//        result.put("email", emailResponse.getBody());
        return ResponseEntity.ok(result);
    }
}
