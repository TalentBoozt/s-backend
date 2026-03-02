package com.talentboozt.s_backend.shared.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper mapper;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    // Model Priority List: If the first fails with 429, try the second.
    private final List<String> modelRotation = List.of(
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite",
            "gemini-1.5-flash"
    );

    public <T> AiResponse<T> callStructuredApiWithRaw(String system, String user, Class<T> responseType, Map<String, Object> schema) {
        return attemptWithModel(0, system, user, responseType, schema);
    }

    private <T> AiResponse<T> attemptWithModel(int modelIndex, String system, String user, Class<T> responseType, Map<String, Object> schema) {
        if (modelIndex >= modelRotation.size()) {
            throw new RuntimeException("All Gemini models exhausted or rate-limited.");
        }

        String currentModel = modelRotation.get(modelIndex);
        log.info("Attempting AI generation with model: {}", currentModel);

        Map<String, Object> requestBody = createRequestBody(system, user, schema);

        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + currentModel + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    // Logic for 429: Retry with backoff OR switch model
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(62)) // Wait > 1 min for Free Tier
                            .filter(this::isRateLimitError)
                            .doBeforeRetry(retry -> log.warn("Model {} rate limited. Retrying in 62s...", currentModel)))
                    .onErrorResume(e -> {
                        if (isRateLimitError(e)) {
                            log.error("Model {} exhausted. Falling back to next model...", currentModel);
                            // Recursively try next model in the list
                            return Mono.empty(); 
                        }
                        return Mono.error(e);
                    })
                    .map(resp -> processResponse(resp, responseType))
                    .blockOptional()
                    .orElseGet(() -> attemptWithModel(modelIndex + 1, system, user, responseType, schema));

        } catch (Exception e) {
            log.error("Request failed for {}: {}", currentModel, e.getMessage());
            return attemptWithModel(modelIndex + 1, system, user, responseType, schema);
        }
    }

    private boolean isRateLimitError(Throwable throwable) {
        return throwable instanceof WebClientResponseException ex && 
               ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
    }

    private Map<String, Object> createRequestBody(String system, String user, Map<String, Object> schema) {
        Map<String, Object> config = new HashMap<>();
        config.put("response_mime_type", "application/json");
        if (schema != null) config.put("response_schema", schema);

        return Map.of(
            "system_instruction", Map.of("parts", List.of(Map.of("text", system))),
            "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", user)))),
            "generation_config", config
        );
    }

    private <T> AiResponse<T> processResponse(Map<String, Object> response, Class<T> responseType) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String rawJson = (String) parts.get(0).get("text");
            String cleaned = rawJson.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();
            return new AiResponse<>(mapper.readValue(cleaned, responseType), cleaned);
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    public record AiResponse<T>(T parsed, String raw) {}
}
