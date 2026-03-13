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
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Component
public class GeminiProvider implements AIProvider {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    private final List<String> modelRotation = List.of(
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite",
            "gemini-1.5-flash"
    );

    public GeminiProvider(
            @Value("${lifeplanner.ai.gemini.api-key:}") String apiKey,
            @Value("${lifeplanner.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            ObjectMapper objectMapper
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public PlanResponse generatePlan(Goal goal, UserProfile userProfile, String prompt) {
        try {
            String system = "You are a structured JSON planner assistant. Always respond with valid JSON only.";
            String raw = attemptWithModel(0, system, prompt, true);
            return objectMapper.readValue(raw, PlanResponse.class);
        } catch (Exception e) {
            log.error("Gemini generatePlan failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to generate plan via Gemini", e);
        }
    }

    @Override
    public String generateJournalPrompt(UserProfile userProfile, String prompt) {
        try {
            String system = "You are a concise journaling coach. Generate a single reflection prompt as a plain string.";
            return attemptWithModel(0, system, prompt, false);
        } catch (Exception e) {
            log.error("Gemini generateJournalPrompt failed: {}", e.getMessage(), e);
            return "What did you learn about yourself today? Reflect on a moment of growth.";
        }
    }

    @Override
    public String generateJournalInsight(UserProfile userProfile, String reflection, UserPreferences prefs) {
        try {
            String system = "You are a wise life coach. Provide a short, insightful, and motivating response (max 3 sentences).";
            String prompt = String.format("Analyze this journal reflection: \"%s\". User background: %s. Preferred style: %s.", 
                reflection, userProfile.getLifestyleData(), prefs.getJournalingStyle());
            return attemptWithModel(0, system, prompt, false);
        } catch (Exception e) {
            log.error("Gemini generateJournalInsight failed: {}", e.getMessage(), e);
            return "Great reflection. Keep focusing on your goals and maintaining consistency.";
        }
    }

    @Override
    public OptimizedScheduleResponse optimizeSchedule(List<String> missedTasks, String prompt) {
        try {
            String system = "You are a structured JSON productivity assistant. Always respond with valid JSON only.";
            String raw = attemptWithModel(0, system, prompt, true);
            return objectMapper.readValue(raw, OptimizedScheduleResponse.class);
        } catch (Exception e) {
            log.error("Gemini optimizeSchedule failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to optimize schedule via Gemini", e);
        }
    }

    private String attemptWithModel(int modelIndex, String system, String user, boolean isJsonResponse) {
        if (modelIndex >= modelRotation.size()) {
            throw new AIProviderException("All Gemini models exhausted or rate-limited.");
        }

        String currentModel = modelRotation.get(modelIndex);
        log.info("Attempting Gemini generation with model: {}", currentModel);

        Map<String, Object> config = new HashMap<>();
        if (isJsonResponse) {
            config.put("response_mime_type", "application/json");
        }
        config.put("temperature", 0.7);
        config.put("maxOutputTokens", 4096);

        Map<String, Object> requestBody = Map.of(
            "system_instruction", Map.of("parts", List.of(Map.of("text", system))),
            "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", user)))),
            "generation_config", config
        );

        try {
            String responseBody = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/" + currentModel + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                if (isJsonResponse) {
                    text = text.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();
                }
                return text;
            }
            throw new AIProviderException("No candidates in Gemini response");
        } catch (Exception e) {
            log.warn("Gemini request failed for model {}: {}", currentModel, e.getMessage());
            return attemptWithModel(modelIndex + 1, system, user, isJsonResponse);
        }
    }
}
