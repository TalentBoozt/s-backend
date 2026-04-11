package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.LLMTaskType;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LLMRouter {

    private final OpenSourceLLMClient openSourceClient;
    private final LLMClient premiumClient;
    private final ObjectMapper objectMapper;

    public LLMRouter(OpenSourceLLMClient openSourceClient, LLMClient premiumClient, ObjectMapper objectMapper) {
        this.openSourceClient = openSourceClient;
        this.premiumClient = premiumClient;
        this.objectMapper = objectMapper;
    }

    public String generate(ESubscriptionPlan plan, LLMTaskType taskType, String system, String user, boolean isJson) {
        log.info("LLM Routing → Plan: {}, Task: {}, Provider Heuristic: {}", plan, taskType,
                taskType == LLMTaskType.VALIDATION ? "PREMIUM" : "OPEN_SOURCE");
        try {
            switch (taskType) {

                case QUIZ:
                case OUTLINE:
                    return attemptOpenSourceWithJsonRetry(plan, system, user, isJson);

                case LESSON:
                case SUMMARY:
                case TRANSLATION:
                case REWRITE:
                case REVISION:
                    String result = openSourceClient.generate(system, user, isJson);
                    if (isLowQuality(result)) {
                        log.warn("Fallback triggered for plan {} → switching to premium model for task {}", plan, taskType);
                        return premiumClient.generate(plan, system, user, isJson);
                    }
                    return result;

                case VALIDATION:
                    return premiumClient.generate(plan, system, user, true);

                default:
                    return premiumClient.generate(plan, system, user, isJson);
            }

        } catch (Exception e) {
            log.warn("Fallback triggered for plan {} → switching to premium model. Error: {}", plan, e.getMessage());
            return premiumClient.generate(plan, system, user, isJson);
        }
    }

    private String attemptOpenSourceWithJsonRetry(ESubscriptionPlan plan, String system, String user, boolean isJson) {
        String response = openSourceClient.generate(system, user, isJson);
        if (isJson && !isValidJson(response)) {
            // retry once
            response = openSourceClient.generate(system, user, isJson);
            if (!isValidJson(response)) {
                log.warn("Fallback triggered for plan {} → switching to premium model due to invalid JSON", plan);
                return premiumClient.generate(plan, system, user, isJson);
            }
        }
        return response;
    }

    private boolean isValidJson(String response) {
        if (response == null || response.trim().isEmpty())
            return false;
        try {
            objectMapper.readTree(response);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLowQuality(String response) {
        return response == null || response.length() < 200 || response.contains("I am an AI");
    }
}
