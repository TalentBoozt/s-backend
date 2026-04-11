package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.dto.LRedditResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Base64;
import java.util.Map;


@Component
public class LRedditClient {

    private static final Logger log = LoggerFactory.getLogger(LRedditClient.class);
    private final RestTemplate restTemplate;

    @Value("${reddit.client-id:}")
    private String clientId;

    @Value("${reddit.client-secret:}")
    private String clientSecret;

    private String accessToken = null;

    public LRedditClient() {
        this.restTemplate = new RestTemplate();
    }

    private void refreshAccessToken() {
        if (clientId == null || clientId.isEmpty()) return;

        try {
            String url = "https://www.reddit.com/api/v1/access_token";
            HttpHeaders headers = new HttpHeaders();
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("User-Agent", "spring:leados.collector:v1.1 (by /u/talnova_bot)");
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() != null) {
                this.accessToken = (String) response.getBody().get("access_token");
                log.info("Successfully refreshed Reddit access token");
            }
        } catch (Exception e) {
            log.error("Failed to refresh Reddit access token", e);
        }
    }

    public LRedditResponseDTO searchSubreddit(String subreddit, String keyword, int limit) {
        try {
            // Use oauth.reddit.com if we have a token, otherwise fallback to standard www.reddit.com
            String baseUrl = (accessToken != null) ? "https://oauth.reddit.com" : "https://www.reddit.com";
            String url = String.format("%s/r/%s/search.json?q=%s&restrict_sr=1&sort=new&limit=%d",
                    baseUrl, subreddit, keyword, limit);
            
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null) {
                headers.set("Authorization", "Bearer " + accessToken);
            } else if (clientId != null && !clientId.isEmpty()) {
                refreshAccessToken();
                if (accessToken != null) {
                    headers.set("Authorization", "Bearer " + accessToken);
                    // Update URL to oauth
                    url = url.replace("www.reddit.com", "oauth.reddit.com");
                }
            }

            headers.set("User-Agent", "spring:leados.collector:v1.1 (by /u/talnova_bot)");
            headers.set("Accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<LRedditResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    LRedditResponseDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            if (e.getMessage().contains("401") && accessToken != null) {
                accessToken = null; // Token likely expired
                return searchSubreddit(subreddit, keyword, limit); // Retry once
            }
            log.error("Failed to fetch from Reddit for subreddit: {} and keyword: {}", subreddit, keyword);
            return null;
        }
    }
}

