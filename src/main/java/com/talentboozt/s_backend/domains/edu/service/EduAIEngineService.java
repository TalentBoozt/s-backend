package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import org.springframework.stereotype.Service;

@Service
public class EduAIEngineService {

    private final EduAICreditService creditService;

    public EduAIEngineService(EduAICreditService creditService) {
        this.creditService = creditService;
    }

    // Emulating LLM API Calls mapping dummy output structures for MVP
    public String generateCourseOutline(String userId, String courseId, AIGenerationRequest request) {
        int tokenCost = 15; // fixed token cost rule for outline mapping
        
        // Emulated LLM output response
        String mockupResponse = """
            {
              "sections": [
                 {
                   "title": "Introduction to %s",
                   "lessons": ["What is it?", "Core Mechanics", "Setting up Environment"]
                 },
                 {
                   "title": "Advanced Strategies",
                   "lessons": ["Optimization Techniques", "Deployment Models"]
                 }
              ]
            }
            """.formatted(request.getTopic());

        // Deduct tokens. If failing, will throw standard error natively defined in CreditService
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_OUTLINE_GENERATION, 
            "Generate " + request.getAudienceLevel() + " outline about " + request.getTopic(), mockupResponse);

        return mockupResponse;
    }

    public String generateLessonContent(String userId, String courseId, String lessonObjective) {
        int tokenCost = 30; // heavy generations cost more logically
        
        String mockupResponse = "### Welcome to this Lesson:\\n\\nToday we cover: " + lessonObjective + "\\n\\n1. **Core Concept**: Understanding variables.\\n2. **Best Practices**: Keep them nested safely.";
        
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_CONTENT_GENERATION, 
            "Write a dynamic long-form article teaching: " + lessonObjective, mockupResponse);
            
        return mockupResponse;
    }

    public String generateSystemQuiz(String userId, String courseId, String topic) {
        int tokenCost = 10;
        
        String mockupResponse = """
            [
              {"question": "What is the primary function?", "options": ["A", "B", "C"], "answer": "B"},
              {"question": "How do you start?", "options": ["X", "Y", "Z"], "answer": "X"}
            ]
            """;
            
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_QUIZ_GENERATION, 
            "Create a 5-question multiple choice quiz on: " + topic, mockupResponse);
            
        return mockupResponse;
    }
}
