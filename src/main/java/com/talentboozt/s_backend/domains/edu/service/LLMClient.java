package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class LLMClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // Models that do NOT support the system_instruction field (Gemini 1.0 line)
    private static final Set<String> GEMINI_NO_SYSTEM_INSTRUCTION = Set.of(
            "gemini-1.0-pro",
            "gemini-1.0-pro-001",
            "gemini-1.0-pro-latest");

    // Models that use the stable /v1/ endpoint instead of /v1beta/
    private static final Set<String> GEMINI_V1_MODELS = Set.of(
            "gemini-1.0-pro",
            "gemini-1.0-pro-001",
            "gemini-1.0-pro-latest",
            "gemini-1.5-pro",
            "gemini-1.5-pro-latest",
            "gemini-1.5-flash",
            "gemini-1.5-flash-latest");

    @Value("${edu.ai.provider:OPENAI}")
    private String provider;

    @Value("${edu.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${edu.ai.temperature:0.7}")
    private double temperature;

    @Value("${edu.ai.openai.api-key:}")
    private String openAiKey;

    @Value("${edu.ai.gemini.api-key:}")
    private String geminiKey;

    @Value("${edu.ai.pro.model:gemini-1.5-flash}")
    private String proModel;

    @Value("${edu.ai.premium.model:gpt-4o}")
    private String premiumModel;

    @Value("${edu.ai.enterprise.model:gpt-4o}")
    private String enterpriseModel;

    public LLMClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Retry(name = "llmRetry", fallbackMethod = "llmFallback")
    public String generate(ESubscriptionPlan plan, String systemPrompt, String userPrompt, boolean isJsonResponse) {
        String effectiveProvider = provider;
        String effectiveModel = model;

        if (plan == ESubscriptionPlan.PRO) {
            effectiveProvider = "GEMINI";
            effectiveModel = proModel;
        } else if (plan == ESubscriptionPlan.PREMIUM) {
            effectiveProvider = "OPENAI";
            effectiveModel = premiumModel;
        } else if (plan == ESubscriptionPlan.ENTERPRISE) {
            effectiveProvider = "OPENAI";
            effectiveModel = enterpriseModel;
        }

        log.info("Generating AI response for plan: {} using provider: {} and model: {}", plan, effectiveProvider,
                effectiveModel);

        if ("GEMINI".equalsIgnoreCase(effectiveProvider)) {
            return callGemini(effectiveModel, systemPrompt, userPrompt, isJsonResponse);
        } else {
            return callOpenAI(effectiveModel, systemPrompt, userPrompt, isJsonResponse);
        }
    }

    private String callOpenAI(String effectiveModel, String systemPrompt, String userPrompt, boolean isJsonResponse) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", effectiveModel);
        requestBody.put("temperature", temperature);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)));

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

            if (response != null && response.has("choices") && response.get("choices").isArray()
                    && !response.get("choices").isEmpty()) {
                String content = response.get("choices").get(0).get("message").get("content").asText();
                return cleanResponse(content, isJsonResponse);
            }
            throw new RuntimeException("Empty response from OpenAI");
        } catch (Exception e) {
            log.error("OpenAI call failed: {}", e.getMessage());
            throw e;
        }
    }

    private String callGemini(String effectiveModel, String systemPrompt, String userPrompt, boolean isJsonResponse) {
        // UNIFIED FIX: Use camelCase for the REST API (v1/v1beta). 
        // Diagnostic logs confirmed snake_case is rejected for these fields.
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", temperature);
        if (isJsonResponse) {
            generationConfig.put("responseMimeType", "application/json");
        }

        Map<String, Object> requestBody = new HashMap<>();
        boolean supportsSystemInstruction = !GEMINI_NO_SYSTEM_INSTRUCTION.contains(effectiveModel);

        if (supportsSystemInstruction && systemPrompt != null && !systemPrompt.isBlank()) {
            requestBody.put("systemInstruction",
                    Map.of("parts", List.of(Map.of("text", systemPrompt))));
            requestBody.put("contents",
                    List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))));
        } else {
            // Merge system prompt into the user message
            String mergedPrompt = (systemPrompt != null && !systemPrompt.isBlank())
                    ? systemPrompt + "\n\n" + userPrompt
                    : userPrompt;
            requestBody.put("contents",
                    List.of(Map.of("role", "user", "parts", List.of(Map.of("text", mergedPrompt)))));
        }

        requestBody.put("generationConfig", generationConfig);

        // FIX 3: Route to /v1/ for stable models, /v1beta/ for preview/experimental
        // ones
        String apiVersion = GEMINI_V1_MODELS.contains(effectiveModel) ? "v1" : "v1beta";

        try {
            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("generativelanguage.googleapis.com")
                            .path("/" + apiVersion + "/models/" + effectiveModel + ":generateContent")
                            .queryParam("key", geminiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> clientResponse.bodyToMono(String.class)
                            .map(errorBody -> new RuntimeException("Gemini API Error: " + errorBody))
                            .flatMap(Mono::error))
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Null response from Gemini");
            }

            // FIX 4: Guard against SAFETY-blocked candidates that have no "parts"
            JsonNode candidates = response.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                // Surface the promptFeedback block when candidates are absent entirely
                String feedback = response.path("promptFeedback").path("blockReason").asText("UNKNOWN");
                throw new RuntimeException("Gemini returned no candidates. Block reason: " + feedback);
            }

            JsonNode firstCandidate = candidates.get(0);
            String finishReason = firstCandidate.path("finishReason").asText("");

            JsonNode parts = firstCandidate.path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                // Candidate exists but was blocked mid-generation (e.g. SAFETY, RECITATION)
                throw new RuntimeException("Gemini candidate has no content parts. finishReason: " + finishReason);
            }

            String content = parts.get(0).path("text").asText();
            if (content.isBlank()) {
                throw new RuntimeException("Gemini returned blank content. finishReason: " + finishReason);
            }

            return cleanResponse(content, isJsonResponse);

        } catch (Exception e) {
            log.error("Gemini call failed (model={}, apiVersion={}): {}", effectiveModel, apiVersion, e.getMessage());
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

    public String llmFallback(ESubscriptionPlan plan, String systemPrompt, String userPrompt, boolean isJsonResponse,
            Throwable t) {
        log.error("LLM Call failed for plan {} after retries. Provider: {}, Error: {}", plan, provider, t.getMessage());
        return isJsonResponse ? "{ \"error\": \"AI service temporarily unavailable\", \"fallback\": true }"
                : "Expert content is currently unavailable. Please try again later.";
    }
}
