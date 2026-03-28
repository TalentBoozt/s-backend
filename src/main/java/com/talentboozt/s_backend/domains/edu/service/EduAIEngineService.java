package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import org.springframework.stereotype.Service;

@Service
public class EduAIEngineService {

    private final EduAICreditService creditService;
    private final LLMClient llmClient;

    public EduAIEngineService(EduAICreditService creditService, LLMClient llmClient) {
        this.creditService = creditService;
        this.llmClient = llmClient;
    }

    public String generateCourseOutline(String userId, String courseId, AIGenerationRequest request) {
        int tokenCost = 15;
        
        String systemPrompt = """
            You are an expert curriculum designer. 
            Respond ONLY with a valid JSON object following this structure:
            {
              "sections": [
                {
                  "title": "Section Title",
                  "lessons": ["Lesson 1", "Lesson 2"]
                }
              ]
            }
            """;
        
        String userPrompt = String.format("Generate a comprehensive course outline for the topic: '%s'. Target audience: %s.", 
            request.getTopic(), request.getAudienceLevel());

        String aiResponse = llmClient.generate(systemPrompt, userPrompt, true);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_OUTLINE_GENERATION, 
            "Generate " + request.getAudienceLevel() + " outline about " + request.getTopic(), aiResponse);

        return aiResponse;
    }

    public String generateLessonContent(String userId, String courseId, String lessonObjective) {
        int tokenCost = 30;
        
        String systemPrompt = "You are a world-class educator. Write detailed, engaging, and professional lesson content in Markdown format.";
        String userPrompt = String.format("Write a detailed lesson teaching: '%s'. Include core concepts and best practices.", lessonObjective);

        String aiResponse = llmClient.generate(systemPrompt, userPrompt, false);
        
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_CONTENT_GENERATION, 
            "Write a dynamic long-form article teaching: " + lessonObjective, aiResponse);
            
        return aiResponse;
    }

    public String generateSystemQuiz(String userId, String courseId, String topic) {
        int tokenCost = 10;
        
        String systemPrompt = """
            You are an educational quiz generator. 
            Respond ONLY with a valid JSON array of objects following this structure:
            [
              {"question": "Question text", "options": ["Option A", "Option B", "Option C"], "answer": "Option B"}
            ]
            """;
        
        String userPrompt = String.format("Create a 5-question multiple choice quiz on the topic: '%s'.", topic);

        String aiResponse = llmClient.generate(systemPrompt, userPrompt, true);
            
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_QUIZ_GENERATION, 
            "Create quiz on: " + topic, aiResponse);
            
        return aiResponse;
    }
}
