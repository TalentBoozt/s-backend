package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.ai_tool.dto.*;
import com.talentboozt.s_backend.shared.ai.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAiClient openAiClient; // HTTP client to OpenAI API

    public CareerPathResponse getCareerPaths(CareerPathRequest req) {
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

    public RoadmapResponse getRoadmap(RoadmapRequest req) {
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

    public ChatResponse chat(ChatRequest req) {
        String prompt = """
                You are a helpful assistant. Keep the response under 60 words.
                Respond strictly in the following JSON format: { "reply": "<your short reply here>" }

                User: %s
                """.formatted(req.getMessage());

        return openAiClient.callStructuredApi(prompt, ChatResponse.class);
    }
}
