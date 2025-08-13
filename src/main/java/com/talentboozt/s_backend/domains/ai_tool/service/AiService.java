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
                Return JSON with:
                educationPaths, skillsToDevelop, qualifications, 
                requiredExperiencesWithAverageYears (as a list of maps with experience name and average years), 
                immediateSteps.
                Keep answers short and practical.
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
