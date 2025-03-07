package com.talentboozt.s_backend.Controller.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {

    @GetMapping(value = "/static/**", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> proxyToSSR(HttpServletRequest request) {
        String apiUrl = "https://talentboozt.com" + request.getRequestURI();
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(apiUrl, String.class);
    }
}
