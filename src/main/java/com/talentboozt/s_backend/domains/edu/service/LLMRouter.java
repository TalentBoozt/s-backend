package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.LLMTaskType;

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

    public String generate(LLMTaskType taskType, String system, String user, boolean isJson) {
        log.info("LLM Routing → Task: {}, Provider: {}", taskType,
                taskType == LLMTaskType.VALIDATION ? "PREMIUM" : "OPEN_SOURCE");
        try {
            switch (taskType) {

                case QUIZ:
                case OUTLINE:
                    return attemptOpenSourceWithJsonRetry(system, user, isJson);

                case LESSON:
                case SUMMARY:
                case TRANSLATION:
                case REWRITE:
                case REVISION:
                    String result = openSourceClient.generate(system, user, isJson);
                    if (isLowQuality(result)) {
                        log.warn("Fallback triggered → switching to premium model for task {}", taskType);
                        return premiumClient.generate(system, user, isJson);
                    }
                    return result;

                case VALIDATION:
                    return premiumClient.generate(system, user, true);

                default:
                    return premiumClient.generate(system, user, isJson);
            }

        } catch (Exception e) {
            log.warn("Fallback triggered → switching to premium model", e);
            return premiumClient.generate(system, user, isJson);
        }
    }

    private String attemptOpenSourceWithJsonRetry(String system, String user, boolean isJson) {
        String response = openSourceClient.generate(system, user, isJson);
        if (isJson && !isValidJson(response)) {
            // retry once
            response = openSourceClient.generate(system, user, isJson);
            if (!isValidJson(response)) {
                log.warn("Fallback triggered → switching to premium model due to invalid JSON");
                return premiumClient.generate(system, user, isJson);
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
