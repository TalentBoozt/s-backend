package com.talentboozt.s_backend.shared.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {

    private final RestTemplate restTemplate;

    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/static/**", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> proxyToSSR(HttpServletRequest request) {
        String apiUrl = "https://talnova.io" + request.getRequestURI();
        return restTemplate.getForEntity(apiUrl, String.class);
    }
}
