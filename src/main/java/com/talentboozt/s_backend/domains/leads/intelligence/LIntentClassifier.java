package com.talentboozt.s_backend.domains.leads.intelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LIntentClassifier {

    private static final Logger log = LoggerFactory.getLogger(LIntentClassifier.class);
    
    @Value("${openai.api.key:stub-key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LIntentClassifier(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String SYSTEM_PROMPT = 
        "You are a Lead Scoring AI for LeadOS. Analyze forum/social content for lead potential.\n" +
        "1. Classify intent: [LEARNING, BUYING, PROBLEM_SOLVING, NOISE].\n" +
        "2. Weights (0-100): intentWeight, engagementWeight, recencyWeight.\n" +
        "3. Sentiment: Number between -1.0 (pain/frustration) and 1.0 (positive/interest).\n" +
        "4. Urgency (0-100): How immediate is the need?\n" +
        "5. Tags: Extract 3-5 relevant keywords.\n\n" +
        "Respond ONLY with raw JSON:\n" +
        "{\n" +
        "  \"intent\": \"BUYING\",\n" +
        "  \"intentWeight\": 90,\n" +
        "  \"engagementWeight\": 60,\n" +
        "  \"recencyWeight\": 100,\n" +
        "  \"sentiment\": 0.2,\n" +
        "  \"urgency\": 85,\n" +
        "  \"tags\": [\"crm\", \"automation\"]\n" +
        "}";

    public LAIAnalysisResult analyzeContent(String content) {
        if (openAiApiKey.equals("stub-key")) {
            log.warn("Using stub API key. Returning dummy response for AI Analysis.");
            return stubAnalysisResult();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", content != null ? content : "")
            ));
            requestBody.put("temperature", 0.0);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String responseContent = (String) message.get("content");
                
                return objectMapper.readValue(responseContent, LAIAnalysisResult.class);
            }
        } catch (Exception e) {
            log.error("Failed to classify intent using OpenAI API", e);
        }
        
        return stubAnalysisResult();
    }

    private LAIAnalysisResult stubAnalysisResult() {
        LAIAnalysisResult stub = new LAIAnalysisResult();
        stub.setIntent("PROBLEM_SOLVING");
        stub.setIntentWeight(85);
        stub.setEngagementWeight(70);
        stub.setRecencyWeight(90);
        stub.setSentiment(-0.4);
        stub.setUrgency(75);
        stub.setTags(List.of("help_needed", "integration"));
        return stub;
    }
}
