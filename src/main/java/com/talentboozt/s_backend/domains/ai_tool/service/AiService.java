package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.ai_tool.dto.*;
import com.talentboozt.s_backend.domains.ai_tool.enums.AIUsageType;
import com.talentboozt.s_backend.domains.ai_tool.service.AIUsageService;
import com.talentboozt.s_backend.shared.ai.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAiClient openAiClient;
    private final AIUsageService aiUsageService;

    public CareerPathResponse getCareerPaths(String userId, CareerPathRequest req) {
        aiUsageService.consumeCredits(userId, AIUsageType.GENERATION, 1);
        String prompt = """
                You are a career guidance AI.
                User's education: %s
                Skills: %s
                Interests: %s
                Return JSON with fields:
                careerPaths, upskillingOptions, jobRoles, courseKeywords.
                Keep answers short and relevant.
                """.formatted(req.getEducation(), req.getSkills(), req.getInterests());

        return openAiClient.callStructuredApi(prompt, CareerPathResponse.class);
    }

    public RoadmapResponse getRoadmap(String userId, RoadmapRequest req) {
        aiUsageService.consumeCredits(userId, AIUsageType.VALIDATION, 1);
        String prompt = """
                    You are a career coach.
                    Dream Job: %s
                    Return the following in JSON format:
                    - educationPaths (list of strings)
                    - skillsToDevelop (list of strings)
                    - qualifications (list of strings)
                    - requiredExperiencesWithAverageYears (list of objects, where each object has: 
                        - experienceName (string)
                        - averageYears (integer, if a range is provided, return the average value)
                    - immediateSteps (list of strings)
                    
                    For "requiredExperiencesWithAverageYears", if a range like "3-5 years" is mentioned, 
                    please return the **average value** (for example, return 4 years instead of "3-5 years").
                    Keep the answers short and practical.
                """.formatted(req.getDreamJob());

        return openAiClient.callStructuredApi(prompt, RoadmapResponse.class);
    }

    public ChatResponse chat(String userId, ChatRequest req) {
        aiUsageService.consumeCredits(userId, AIUsageType.GENERATION, 1);
        String prompt = """
                You are a helpful assistant. Keep the response under 60 words.
                Respond strictly in the following JSON format: { "reply": "<your short reply here>" }

                User: %s
                """.formatted(req.getMessage());

        return openAiClient.callStructuredApi(prompt, ChatResponse.class);
    }

    public AiGeneratedSummary generateReleaseSummary(String userId, String content) {
        aiUsageService.consumeCredits(userId, AIUsageType.GENERATION, 1);
        // Simple truncation to stay within typical context limits if needed
        String truncated = content.length() > 5000 ? content.substring(0, 5000) : content;

        String prompt = """
                You are a SaaS product release analyst.
                Analyze the following release notes or content and provide a structured summary in JSON.

                Content:
                %s

                Response Format (JSON):
                {
                  "summary": "A concise 2-3 sentence summary.",
                  "highlights": ["Point 1", "Point 2", "Point 3"],
                  "snippet": "A short 1-line social teaser.",
                  "seoDescription": "150-160 character SEO description."
                }
                """.formatted(truncated);

        return openAiClient.callStructuredApi(prompt, AiGeneratedSummary.class);
    }
}
