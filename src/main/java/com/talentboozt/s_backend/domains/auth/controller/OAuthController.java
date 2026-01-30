package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v2/oauth")
public class OAuthController {
    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ConfigUtility configUtil;

    private final RestTemplate restTemplate;

    public OAuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/callback/google", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<CredentialsModel> handleGoogleCallback(
            @RequestBody Map<String, String> body,
            HttpServletResponse response) throws IOException {
        System.out.println(body);

        String code = body.get("code");
        String codeVerifier = body.get("codeVerifier");

        if (code == null || codeVerifier == null) {
            return ResponseEntity.badRequest().body(null); // Return 400 if parameters are missing
        }

        String accessToken = exchangeCodeForAccessToken(code, codeVerifier);

        if (accessToken != null) {
            OidcUser oidcUser = fetchUserInfo(accessToken);

            String email = oidcUser.getEmail();
            String firstName = oidcUser.getGivenName();
            String lastName = oidcUser.getFamilyName();

            // Set the access token in a cookie
            response.addCookie(new Cookie("access_token", accessToken));
            response.sendRedirect(configUtil.getProperty("SUCCESS_REDIRECT"));

            CredentialsModel existingCredentials = credentialsService.getCredentialsByEmail(email);
            if (existingCredentials != null) {
                return loginUser(existingCredentials);
            } else {
                return registerGoogleUser(email, firstName, lastName);
            }
        } else {
            response.sendRedirect(configUtil.getProperty("FAILURE_REDIRECT"));
            return null;
        }
    }

    private OidcUser fetchUserInfo(String accessToken) {
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(accessToken));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<OidcUser> response = restTemplate.exchange(userInfoEndpoint, Objects.requireNonNull(HttpMethod.GET), entity, OidcUser.class);

        return response.getBody();
    }

    private ResponseEntity<CredentialsModel> loginUser(CredentialsModel credentials) {
        credentials.setRole("candidate");
        credentials.setUserLevel("1");
        return ResponseEntity.ok(credentials);
    }

    private ResponseEntity<CredentialsModel> registerGoogleUser(String email, String firstName, String lastName) {
        CredentialsModel newUser = new CredentialsModel();
        newUser.setEmail(email);
        newUser.setFirstname(firstName);
        newUser.setLastname(lastName);
        newUser.setRole("candidate");
        newUser.setUserLevel("1");

        CredentialsModel savedUser = credentialsService.addCredentials(newUser, null, null);
        return ResponseEntity.ok(savedUser);
    }

    private String exchangeCodeForAccessToken(String code, String codeVerifier) {
        String url = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("client_id", configUtil.getProperty("GOOGLE_CLIENT_ID"));
        parameters.add("client_secret", configUtil.getProperty("GOOGLE_CLIENT_SECRET"));
        parameters.add("redirect_uri", configUtil.getProperty("GOOGLE_CLIENT_REDIRECT"));
        parameters.add("grant_type", "authorization_code");
        parameters.add("code_verifier", codeVerifier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                return body.get("access_token").toString();
            } else {
                // Log error message if access_token is not present
                System.out.println("Error in response: " + body);
            }
        } catch (Exception e) {
            // Log the exception stack trace
            e.printStackTrace();
        }
        return null;
    }
}
