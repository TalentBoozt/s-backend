package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class OpenSourceLLMClient {

    private final WebClient webClient;

    @Value("${edu.ai.opensource.api-key}")
    private String apiKey;

    public OpenSourceLLMClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.together.xyz").build();
    }

    public String generate(String systemPrompt, String userPrompt, boolean isJson) {

        Map<String, Object> body = Map.of(
            "model", "mistralai/Mistral-7B-Instruct-v0.1",
            "prompt", systemPrompt + "\n" + userPrompt,
            "max_tokens", 500,
            "temperature", 0.7
        );

        JsonNode response = webClient.post()
            .uri("/v1/completions")
            .header("Authorization", "Bearer " + apiKey)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        return response.get("choices").get(0).get("text").asText();
    }
}
