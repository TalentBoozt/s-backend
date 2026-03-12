package com.talentboozt.s_backend.domains.lifeplanner.ai.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AICacheService {

    private final AICacheEntryRepository cacheEntryRepository;
    private final ObjectMapper objectMapper;

    public Optional<PlanResponse> getCachedPlan(String prompt, String provider) {
        String key = buildCacheKey(prompt, provider);
        return cacheEntryRepository.findByCacheKey(key)
                .map(entry -> {
                    try {
                        return objectMapper.readValue(entry.getResponseJson(), PlanResponse.class);
                    } catch (Exception e) {
                        log.warn("Failed to deserialize cached plan response, evicting: {}", key);
                        cacheEntryRepository.delete(entry);
                        return null;
                    }
                });
    }

    public void cachePlan(String prompt, String provider, PlanResponse response) {
        try {
            String key = buildCacheKey(prompt, provider);
            String json = objectMapper.writeValueAsString(response);

            AICacheEntry entry = cacheEntryRepository.findByCacheKey(key)
                    .orElse(new AICacheEntry());
            entry.setCacheKey(key);
            entry.setResponseJson(json);
            entry.setProvider(provider);
            entry.setPromptHash(sha256(prompt));
            entry.setCreatedAt(Instant.now());

            cacheEntryRepository.save(entry);
            log.info("Cached AI plan response under key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to cache AI plan response: {}", e.getMessage());
        }
    }

    private String buildCacheKey(String prompt, String provider) {
        return provider + ":" + sha256(prompt);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }
}
