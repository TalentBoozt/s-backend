package com.talentboozt.s_backend.shared.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient {
    @Value("${gemini.api.key:default_key}")
    private String apiKey;

    private final ObjectMapper mapper;

    public <T> AiResponse<T> callStructuredApiWithRaw(String systemInstruction, String prompt, Class<T> responseType) {
        WebClient client = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "role", "system",
                        "parts", List.of(Map.of("text", systemInstruction))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"));

        String raw;
        try {
            Map<String, Object> res = client.post()
                    .uri(builder -> builder
                            .path("/v1beta/models/gemini-1.5-pro:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (res != null && res.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.get("candidates");
                Map<String, Object> firstCandidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                Map<String, Object> firstPart = parts.get(0);
                raw = (String) firstPart.get("text");
            } else {
                throw new RuntimeException("Unexpected response from Gemini API: " + res);
            }
        } catch (Exception e) {
            throw new RuntimeException("Gemini API call failed", e);
        }

        String cleaned = raw.replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
        try {
            T parsed = mapper.readValue(cleaned, responseType);
            return new AiResponse<>(parsed, raw);
        } catch (Exception e) {
            throw new RuntimeException("AI returned invalid JSON: " + raw, e);
        }
    }

    public record AiResponse<T>(T parsed, String raw) {
    }
}
