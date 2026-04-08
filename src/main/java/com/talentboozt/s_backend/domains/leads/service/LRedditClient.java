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

@Component
public class LRedditClient {

    private static final Logger log = LoggerFactory.getLogger(LRedditClient.class);
    private final RestTemplate restTemplate;

    public LRedditClient() {
        this.restTemplate = new RestTemplate();
    }

    public LRedditResponseDTO searchSubreddit(String subreddit, String keyword, int limit) {
        try {
            String url = String.format("https://www.reddit.com/r/%s/search.json?q=%s&restrict_sr=1&sort=new&limit=%d",
                    subreddit, keyword, limit);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "LeadOS-CollectorService/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<LRedditResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    LRedditResponseDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch from Reddit for subreddit: {} and keyword: {}. Error: {}", subreddit, keyword, e.getMessage());
            return null;
        }
    }
}
