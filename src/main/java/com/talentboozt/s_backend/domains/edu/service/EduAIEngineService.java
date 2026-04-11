package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.LLMTaskType;

import org.springframework.stereotype.Service;

@Service
public class EduAIEngineService {

    private final EduAICreditService creditService;
    private final LLMRouter llmRouter;

    public EduAIEngineService(EduAICreditService creditService, LLMRouter llmRouter) {
        this.creditService = creditService;
        this.llmRouter = llmRouter;
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

        String userPrompt = String.format(
                "Generate a comprehensive course outline for the topic: '%s'. Target audience: %s.",
                request.getTopic(), request.getAudienceLevel());

        String aiResponse = llmRouter.generate(LLMTaskType.OUTLINE, systemPrompt, userPrompt, true);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_OUTLINE_GENERATION,
                "Generate " + request.getAudienceLevel() + " outline about " + request.getTopic(), aiResponse);

        return aiResponse;
    }

    public String generateLessonContent(String userId, String courseId, String lessonObjective) {
        int tokenCost = 30;

        String systemPrompt = "You are a world-class educator. Write detailed, engaging, and professional lesson content in Markdown format.";
        String userPrompt = String.format(
                "Write a detailed lesson teaching: '%s'. Include core concepts and best practices.", lessonObjective);

        String aiResponse = llmRouter.generate(LLMTaskType.LESSON, systemPrompt, userPrompt, false);

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

        String aiResponse = llmRouter.generate(LLMTaskType.QUIZ, systemPrompt, userPrompt, true);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_QUIZ_GENERATION,
                "Create quiz on: " + topic, aiResponse);

        return aiResponse;
    }
    public String generateCourseSummary(String userId, String courseId, String courseContext) {
        int tokenCost = 10;
        String systemPrompt = "You are a professional educational copywriter. Write a compelling, concise course summary and marketing description based on the provided course modules and syllabus.";
        String userPrompt = "Course syllabus context:\n" + courseContext;

        String aiResponse = llmRouter.generate(LLMTaskType.SUMMARY, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_SUMMARY_GENERATION,
                "Generate course summary description", aiResponse);

        return aiResponse;
    }

    public String translateCourseContent(String userId, String courseId, String content, String language) {
        int tokenCost = 15;
        String systemPrompt = "You are an expert technical translator. Translate the given lesson content exactly into " + language + ", maintaining markdown formatting, technical accuracy, and tone. Do NOT add any extra conversational text.";
        String userPrompt = "Translate this content:\n" + content;

        String aiResponse = llmRouter.generate(LLMTaskType.TRANSLATION, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_TRANSLATION,
                "Translate lesson to " + language, aiResponse);

        return aiResponse;
    }

    public String rewriteContent(String userId, String courseId, String content, String style) {
        int tokenCost = 15;
        String systemPrompt = "You are an expert editor. Rewrite the following lesson content to make it " + style + ". Maintain the core learning objectives, markdown formatting, and technical accuracy.";
        String userPrompt = "Rewrite this content:\n" + content;

        String aiResponse = llmRouter.generate(LLMTaskType.REWRITE, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_REWRITE,
                "Rewrite lesson content to " + style, aiResponse);

        return aiResponse;
    }

    public String reviseContent(String userId, String courseId, String content) {
        int tokenCost = 10;
        String systemPrompt = "You are an expert technical editor. Proofread and revise the following lesson content. Fix any grammatical errors, improve clarity and flow, and maintain markdown formatting. Return the polished content directly.";
        String userPrompt = "Revise this content:\n" + content;

        String aiResponse = llmRouter.generate(LLMTaskType.REVISION, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_REVISION,
                "Revise and proofread lesson content", aiResponse);

        return aiResponse;
    }
}
