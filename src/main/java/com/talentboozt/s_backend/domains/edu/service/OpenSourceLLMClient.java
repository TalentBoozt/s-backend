package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenSourceLLMClient {

    private final WebClient webClient;

    @Value("${edu.ai.opensource.api-key:}")
    private String apiKey;

    // FIX 1: Together.ai instruction models are chat-based — use /v1/chat/completions,
    // not /v1/completions (which is for base/completion-style models only).
    // Sending a chat model to the completions endpoint returns 401/404 depending on the model.
    private static final String CHAT_ENDPOINT = "/v1/chat/completions";

    @Value("${edu.ai.opensource.model:mistralai/Mistral-7B-Instruct-v0.3}")
    private String model;

    public OpenSourceLLMClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.together.xyz").build();
    }

    public String generate(String systemPrompt, String userPrompt, boolean isJson) {
        // FIX 2: Validate key properly — covers null, blank, placeholder, and unresolved Spring expressions
        if (apiKey == null || apiKey.isBlank()
                || apiKey.equalsIgnoreCase("APIKEY")
                || apiKey.startsWith("${")) {
            throw new RuntimeException("OpenSource AI provider key is not configured.");
        }

        // FIX 3: Use chat message format — required by Instruct/chat-tuned models.
        // The old "prompt" field is for legacy completion models only.
        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user",   "content", userPrompt)
            ),
            "max_tokens", 1024,
            "temperature", 0.7
        );

        try {
            JsonNode response = webClient.post()
                .uri(CHAT_ENDPOINT)
                // FIX 4: Explicitly set Content-Type — Together.ai returns 401 when it
                // is missing or incorrect because it can't parse the request body.
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            if (response == null) {
                throw new RuntimeException("Null response from Together.ai");
            }

            JsonNode choices = response.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("Together.ai returned no choices. Response: " + response);
            }

            // Chat completions return message.content, not text
            String content = choices.get(0).path("message").path("content").asText();
            if (content.isBlank()) {
                String finishReason = choices.get(0).path("finish_reason").asText("unknown");
                throw new RuntimeException("Together.ai returned blank content. finish_reason: " + finishReason);
            }

            return content.trim();

        } catch (WebClientResponseException e) {
            log.error("Together.ai HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Together.ai call failed: {}", e.getMessage());
            throw e;
        }
    }
}