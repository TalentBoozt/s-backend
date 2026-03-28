package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LLMClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${edu.ai.provider:OPENAI}") // OPENAI or GEMINI
    private String provider;

    @Value("${edu.ai.model:gpt-4o-mini}") // gpt-4o-mini or gemini-2.5-flash
    private String model;

    @Value("${edu.ai.temperature:0.7}") // 0.0 to 1.0
    private double temperature;

    @Value("${edu.ai.openai.api-key:}")
    private String openAiKey;

    @Value("${edu.ai.gemini.api-key:}")
    private String geminiKey;

    public LLMClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Retry(name = "llmRetry", fallbackMethod = "llmFallback")
    public String generate(String systemPrompt, String userPrompt, boolean isJsonResponse) {
        log.info("Generating AI response using provider: {} and model: {}", provider, model);
        if ("GEMINI".equalsIgnoreCase(provider)) {
            return callGemini(systemPrompt, userPrompt, isJsonResponse);
        } else {
            return callOpenAI(systemPrompt, userPrompt, isJsonResponse);
        }
    }

    private String callOpenAI(String systemPrompt, String userPrompt, boolean isJsonResponse) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        if (isJsonResponse) {
            requestBody.put("response_format", Map.of("type", "json_object"));
        }

        try {
            JsonNode response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("choices") && response.get("choices").isArray() && !response.get("choices").isEmpty()) {
                String content = response.get("choices").get(0).get("message").get("content").asText();
                return cleanResponse(content, isJsonResponse);
            }
            throw new RuntimeException("Empty response from OpenAI");
        } catch (Exception e) {
            log.error("OpenAI call failed: {}", e.getMessage());
            throw e;
        }
    }

    private String callGemini(String systemPrompt, String userPrompt, boolean isJsonResponse) {
        Map<String, Object> config = new HashMap<>();
        if (isJsonResponse) {
            config.put("response_mime_type", "application/json");
        }
        config.put("temperature", temperature);

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))),
                "generation_config", config
        );

        try {
            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("generativelanguage.googleapis.com")
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", geminiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("candidates") && response.get("candidates").isArray() && !response.get("candidates").isEmpty()) {
                JsonNode parts = response.get("candidates").get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    String content = parts.get(0).path("text").asText();
                    return cleanResponse(content, isJsonResponse);
                }
            }
            throw new RuntimeException("Empty response from Gemini");
        } catch (Exception e) {
            log.error("Gemini call failed: {}", e.getMessage());
            throw e;
        }
    }

    private String cleanResponse(String content, boolean isJsonResponse) {
        if (isJsonResponse) {
            // Remove markdown code blocks if present
            String cleaned = content.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();
            try {
                // Validate JSON structure
                objectMapper.readTree(cleaned);
                return cleaned;
            } catch (Exception e) {
                log.error("Invalid JSON returned by AI: {}", content);
                throw new RuntimeException("AI returned invalid JSON structure", e);
            }
        }
        return content.trim();
    }

    public String llmFallback(String systemPrompt, String userPrompt, boolean isJsonResponse, Throwable t) {
        log.error("LLM Call failed after retries. Provider: {}, Error: {}", provider, t.getMessage());
        return isJsonResponse ? "{ \"error\": \"AI service temporarily unavailable\", \"fallback\": true }" : "Expert content is currently unavailable. Please try again later.";
    }
}
