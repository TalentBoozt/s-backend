package com.talentboozt.s_backend.shared.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    @Value("${gemini.api.key:default_key}")
    private String apiKey;

    private final ObjectMapper mapper;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public <T> AiResponse<T> callStructuredApiWithRaw(
            String systemInstruction,
            String userPrompt,
            Class<T> responseType) {
        return callStructuredApiWithRaw(systemInstruction, userPrompt, responseType, null);
    }

    /**
     * Calls Gemini with structured (JSON) output and robust error handling.
     */
    public <T> AiResponse<T> callStructuredApiWithRaw(
            String systemInstruction,
            String userPrompt,
            Class<T> responseType,
            Map<String, Object> responseSchema) {

        if (apiKey == null || "default_key".equals(apiKey)) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        // Configuration
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("response_mime_type", "application/json");

        // Use temperature 0.0 for strict schema, 0.1 for loose JSON to allow creativity
        generationConfig.put("temperature", responseSchema != null ? 0.0 : 0.1);

        if (responseSchema != null) {
            generationConfig.put("response_schema", responseSchema);
        }

        // Request Body construction
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", systemInstruction))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))),
                "generation_config", generationConfig,
                "safety_settings", List.of(
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE")));

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.0-flash:generateContent") // Stable 2026 endpoint
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return processResponse(response, responseType);

        } catch (WebClientResponseException e) {
            log.error("Gemini API HTTP Error: {} - Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Gemini API failed with HTTP " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Gemini processing failed", e);
            throw new RuntimeException("Gemini API call or parsing failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> AiResponse<T> processResponse(Map<String, Object> response, Class<T> responseType) throws Exception {
        if (response == null)
            throw new RuntimeException("Empty response from Gemini");

        // 1. Handle Candidate extraction
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            Object feedback = response.get("promptFeedback");
            throw new RuntimeException("No candidates returned. Possible safety block. Feedback: " + feedback);
        }

        Map<String, Object> candidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) candidate.get("content");

        // 2. Validate content presence (Fixes the "Generation stopped early: null"
        // error)
        if (content == null || !content.containsKey("parts")) {
            String finishReason = (String) candidate.get("finish_reason");
            Object safetyRatings = candidate.get("safetyRatings");
            throw new RuntimeException(
                    "AI stopped before generating content. Reason: " + finishReason + ". Safety: " + safetyRatings);
        }

        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String rawJson = (String) parts.get(0).get("text");

        // 3. Clean Markdown blocks if present
        String cleaned = rawJson.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();

        // 4. Map to Java Object
        T parsed = mapper.readValue(cleaned, responseType);

        return new AiResponse<>(parsed, cleaned);
    }

    public record AiResponse<T>(T parsed, String raw) {
    }
}
