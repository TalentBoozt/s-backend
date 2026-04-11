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

@Slf4j
@Service
public class LLMClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // THE ROOT CAUSE OF ALL PREVIOUS ERRORS:
    //
    // Google's Gemini REST API has two endpoint families with DIFFERENT field support:
    //
    //   /v1/      → gemini-1.0-pro ONLY. This is the stripped-down stable surface.
    //               Does NOT support: system_instruction, response_mime_type,
    //               or most generationConfig fields. Using these fields returns 400.
    //
    //   /v1beta/  → ALL other models (gemini-1.5-*, gemini-2.0-*, etc.)
    //               DOES support: system_instruction, response_mime_type,
    //               generationConfig, and all modern features.
    //
    // Previous attempts routed gemini-1.5-flash to /v1/ which rejected system_instruction
    // and response_mime_type with 400 INVALID_ARGUMENT. The fix is to invert the logic:
    // ONLY gemini-1.0-* goes to /v1/, everything else uses /v1beta/.
    //
    // Field naming: the /v1beta/ REST API uses snake_case throughout.
    // "generationConfig" (camelCase) and "responseMimeType" are SDK/gRPC conventions
    // and are NOT accepted by the REST endpoint — use "generation_config" and
    // "response_mime_type" instead.

    private static String resolveGeminiApiVersion(String model) {
        // Only the legacy gemini-1.0-pro line uses /v1/
        // Every other model (1.5-flash, 1.5-pro, 2.0-flash, exp models, etc.) uses /v1beta/
        if (model != null && model.startsWith("gemini-1.0-")) {
            return "v1";
        }
        return "v1beta";
    }

    private static boolean supportsSystemInstruction(String model) {
        // gemini-1.0-* models do not support system_instruction (field not in /v1/ schema)
        // All other models support it via /v1beta/
        return model == null || !model.startsWith("gemini-1.0-");
    }

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
        String apiVersion = resolveGeminiApiVersion(effectiveModel);
        boolean useSystemInstruction = supportsSystemInstruction(effectiveModel);

        // REST API uses snake_case throughout — "generation_config" and "response_mime_type"
        // camelCase variants (generationConfig, responseMimeType) are SDK/gRPC only and
        // are silently rejected or cause 400 on the REST endpoint.
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", temperature);
        if (isJsonResponse && useSystemInstruction) {
            // response_mime_type only supported in /v1beta/ (gemini-1.5+ models)
            generationConfig.put("response_mime_type", "application/json");
        }

        Map<String, Object> requestBody = new HashMap<>();

        if (useSystemInstruction && systemPrompt != null && !systemPrompt.isBlank()) {
            requestBody.put("system_instruction",
                    Map.of("parts", List.of(Map.of("text", systemPrompt))));
            requestBody.put("contents",
                    List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))));
        } else {
            // Fold system prompt into user turn for gemini-1.0-* which lacks system_instruction
            String mergedPrompt = (systemPrompt != null && !systemPrompt.isBlank())
                    ? systemPrompt + "\n\n" + userPrompt
                    : userPrompt;
            requestBody.put("contents",
                    List.of(Map.of("role", "user", "parts", List.of(Map.of("text", mergedPrompt)))));
        }

        requestBody.put("generation_config", generationConfig);

        log.debug("Gemini request: model={}, apiVersion=/{}/", effectiveModel, apiVersion);

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
                    .onStatus(status -> status.isError(), clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(body -> new RuntimeException("Gemini API Error: " + body))
                            .flatMap(Mono::error))
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Null response from Gemini");
            }

            JsonNode candidates = response.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                String feedback = response.path("promptFeedback").path("blockReason").asText("UNKNOWN");
                throw new RuntimeException("Gemini returned no candidates. Block reason: " + feedback);
            }

            JsonNode firstCandidate = candidates.get(0);
            String finishReason = firstCandidate.path("finishReason").asText("");

            JsonNode parts = firstCandidate.path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
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
            String cleaned = content.replaceAll("(?s)^\\s*```(?:json)?\\s*(.*?)\\s*```\\s*$", "$1").trim();
            try {
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
