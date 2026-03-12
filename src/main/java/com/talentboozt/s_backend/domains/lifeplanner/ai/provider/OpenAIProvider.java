package com.talentboozt.s_backend.domains.lifeplanner.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import com.talentboozt.s_backend.domains.lifeplanner.shared.exception.AIProviderException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Component
public class OpenAIProvider implements AIProvider {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    public OpenAIProvider(
            @Value("${lifeplanner.ai.openai.api-key:}") String apiKey,
            @Value("${lifeplanner.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${lifeplanner.ai.openai.model:gpt-4o-mini}") String model,
            @Value("${lifeplanner.ai.openai.max-tokens:4096}") int maxTokens,
            @Value("${lifeplanner.ai.openai.temperature:0.7}") double temperature,
            ObjectMapper objectMapper
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    @Override
    public PlanResponse generatePlan(Goal goal, UserProfile userProfile, String prompt) {
        try {
            String rawJson = callChatCompletion(prompt);
            return objectMapper.readValue(rawJson, PlanResponse.class);
        } catch (Exception e) {
            log.error("OpenAI generatePlan failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to generate plan via OpenAI", e);
        }
    }

    @Override
    public String generateJournalPrompt(UserProfile userProfile, String prompt) {
        try {
            return callChatCompletion(prompt);
        } catch (Exception e) {
            log.error("OpenAI generateJournalPrompt failed: {}", e.getMessage(), e);
            return "How was your day? Reflect on your main accomplishments and challenges.";
        }
    }

    @Override
    public OptimizedScheduleResponse optimizeSchedule(List<String> missedTasks, String prompt) {
        try {
            String rawJson = callChatCompletion(prompt);
            return objectMapper.readValue(rawJson, OptimizedScheduleResponse.class);
        } catch (Exception e) {
            log.error("OpenAI optimizeSchedule failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to optimize schedule via OpenAI", e);
        }
    }

    private String callChatCompletion(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a structured JSON planner assistant. Always respond with valid JSON only."),
                Map.of("role", "user", "content", prompt)
        ));

        String responseBody = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response: {}", responseBody, e);
            throw new AIProviderException("Failed to parse OpenAI response", e);
        }
    }
}
