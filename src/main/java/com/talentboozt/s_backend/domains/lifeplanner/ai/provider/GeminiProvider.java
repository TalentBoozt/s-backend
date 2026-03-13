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
    private final String model;
    private final String apiKey;

    public GeminiProvider(
            @Value("${lifeplanner.ai.gemini.api-key:}") String apiKey,
            @Value("${lifeplanner.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            @Value("${lifeplanner.ai.gemini.model:gemini-2.0-flash}") String model,
            ObjectMapper objectMapper
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
        this.model = model;
        this.apiKey = apiKey;
    }

    @Override
    public PlanResponse generatePlan(Goal goal, UserProfile userProfile, String prompt) {
        try {
            String rawJson = callGemini(prompt, true);
            return objectMapper.readValue(rawJson, PlanResponse.class);
        } catch (Exception e) {
            log.error("Gemini generatePlan failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to generate plan via Gemini", e);
        }
    }

    @Override
    public String generateJournalPrompt(UserProfile userProfile, String prompt) {
        try {
            return callGemini(prompt, false);
        } catch (Exception e) {
            log.error("Gemini generateJournalPrompt failed: {}", e.getMessage(), e);
            return "What did you learn about yourself today? Reflect on a moment of growth.";
        }
    }

    @Override
    public String generateJournalInsight(UserProfile userProfile, String reflection, UserPreferences prefs) {
        try {
            String prompt = String.format("Analyze this journal reflection: \"%s\". Provide a short, insightful, and motivating response (max 3 sentences) in the style of a wise life coach. User's background: %s. Preferred journaling style: %s.", 
                reflection, userProfile.getLifestyleData(), prefs.getJournalingStyle());
            return callGemini(prompt, false);
        } catch (Exception e) {
            log.error("Gemini generateJournalInsight failed: {}", e.getMessage(), e);
            return "Great reflection. Keep focusing on your goals and maintaining consistency.";
        }
    }

    @Override
    public OptimizedScheduleResponse optimizeSchedule(List<String> missedTasks, String prompt) {
        try {
            String rawJson = callGemini(prompt, true);
            return objectMapper.readValue(rawJson, OptimizedScheduleResponse.class);
        } catch (Exception e) {
            log.error("Gemini optimizeSchedule failed: {}", e.getMessage(), e);
            throw new AIProviderException("Failed to optimize schedule via Gemini", e);
        }
    }

    private String callGemini(String prompt, boolean isJsonResponse) {
        String systemPrefix = isJsonResponse ? "You are a structured JSON planner assistant. Always respond with valid JSON only.\n\n" : "";
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", systemPrefix + prompt)
                ))
        ));
        
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.7);
        config.put("maxOutputTokens", 4096);
        if (isJsonResponse) {
            config.put("responseMimeType", "application/json");
        }
        requestBody.put("generationConfig", config);

        String uri = "/models/" + model + ":generateContent?key=" + apiKey;

        String responseBody = webClient.post()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            }
            throw new AIProviderException("No candidates in Gemini response");
        } catch (AIProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", responseBody, e);
            throw new AIProviderException("Failed to parse Gemini response", e);
        }
    }
}
