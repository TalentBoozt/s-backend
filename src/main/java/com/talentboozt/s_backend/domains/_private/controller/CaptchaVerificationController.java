package com.talentboozt.s_backend.domains._private.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/security")
public class CaptchaVerificationController {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate;

    public CaptchaVerificationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/verify-captcha")
public ResponseEntity<?> verifyCaptcha(
        @RequestBody Map<String, String> payload,
        HttpSession session) {

    String captchaResponse = payload.get("captchaToken");
    String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", recaptchaSecret);
    params.add("response", captchaResponse);

    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            verifyUrl,
            Objects.requireNonNull(HttpMethod.POST),
            new HttpEntity<>(Objects.requireNonNull(params)),
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

    Map<String, Object> body =
        Objects.requireNonNull(response.getBody(), "Response body is null");

    Boolean success =
        (Boolean) Objects.requireNonNull(body.get("success"), "Success is null");

    if (success) {
        session.setAttribute("captchaVerified", true);
        session.setAttribute("captchaVerifiedAt", Instant.now());
        return ResponseEntity.ok().build();
    } else {
        System.out.println("CAPTCHA failed: " + body.get("error-codes"));
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("CAPTCHA failed");
    }
}
}

