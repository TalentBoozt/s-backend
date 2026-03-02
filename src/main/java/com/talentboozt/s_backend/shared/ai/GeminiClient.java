package com.talentboozt.s_backend.shared.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

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
     * Calls Gemini with structured (JSON) output.
     *
     * @param systemInstruction System-level instruction (e.g. "You are a strict
     *                          article validator...")
     * @param userPrompt        The actual user prompt/content to analyze
     * @param responseType      The Java class/type to deserialize the JSON into
     * @param responseSchema    Optional JSON Schema (Map) — pass null for loose
     *                          JSON mode
     * @param <T>               The response type
     * @return AiResponse with parsed object and raw JSON string
     * @throws RuntimeException on API failure (with detailed Google error message)
     */
    public <T> AiResponse<T> callStructuredApiWithRaw(
            String systemInstruction,
            String userPrompt,
            Class<T> responseType,
            Map<String, Object> responseSchema) {

        if (apiKey == null || apiKey.trim().isEmpty() || "default_key".equals(apiKey)) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        // Enforce clean JSON-only output
        String enforcedPrompt = userPrompt + "\n\n"
                + "Respond **ONLY** with valid JSON. "
                + "Do NOT include any markdown, code blocks (```), explanations, comments, or extra text. "
                + "The response must be pure, parseable JSON matching the requested schema (if provided).";

        Map<String, Object> generationConfig = Map.ofEntries(
                Map.entry("response_mime_type", "application/json"),
                Map.entry("temperature", 0.1),
                Map.entry("top_p", 0.95),
                Map.entry("max_output_tokens", 2048));

        if (responseSchema != null) {
            generationConfig = Map.ofEntries(
                    Map.entry("response_mime_type", "application/json"),
                    Map.entry("response_schema", responseSchema),
                    Map.entry("temperature", 0.0), // stricter with schema
                    Map.entry("top_p", 0.9),
                    Map.entry("max_output_tokens", 2048));
        }

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", enforcedPrompt)))),
                "generation_config", generationConfig,
                "safety_settings", List.of( // optional but helps avoid 400/403 in some cases
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE")));

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-1.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("candidates")) {
                throw new RuntimeException("Invalid Gemini response - no candidates: " + response);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates.isEmpty()) {
                throw new RuntimeException("Gemini returned no candidates");
            }

            Map<String, Object> candidate = candidates.get(0);
            String finishReason = (String) candidate.get("finish_reason");
            if (!"STOP".equals(finishReason)) {
                throw new RuntimeException("Generation stopped early: " + finishReason);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts.isEmpty() || !parts.get(0).containsKey("text")) {
                throw new RuntimeException("No text part in Gemini response");
            }

            String rawJson = (String) parts.get(0).get("text");

            // Robust cleanup of potential markdown blocks
            String cleaned = rawJson.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();

            T parsed = mapper.readValue(cleaned, responseType);

            return new AiResponse<>(parsed, rawJson);

        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            throw new RuntimeException(
                    "Gemini API failed with HTTP " + e.getStatusCode() + ":\n" + errorBody, e);
        } catch (Exception e) {
            throw new RuntimeException("Gemini API call or parsing failed", e);
        }
    }

    public record AiResponse<T>(T parsed, String raw) {
    }
}