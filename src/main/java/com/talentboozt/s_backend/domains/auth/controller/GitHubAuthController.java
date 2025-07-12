package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/github")
public class GitHubAuthController {
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

    @Autowired
    private ConfigUtility configUtil;

    @PostMapping("/token")
    public ResponseEntity<?> exchangeCodeForToken(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        // Prepare the request to exchange the code for a token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Construct the request body for GitHub
        String requestBody = String.format("client_id=%s&client_secret=%s&code=%s",
                configUtil.getProperty("GITHUB_CLIENT_ID"),
                configUtil.getProperty("GITHUB_CLIENT_SECRET"),
                code);

        // Create the entity for the request
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GITHUB_TOKEN_URL, entity, String.class);

            // Handle the response from GitHub
            if (response.getStatusCode() == HttpStatus.OK) {
                // Extract access token from response body (GitHub returns it as a plain text)
                String accessToken = response.getBody().split("&")[0].split("=")[1];
                return ResponseEntity.ok(Map.of("accessToken", accessToken));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error fetching access token");
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getResponseBodyAsString());
        }
    }
}
