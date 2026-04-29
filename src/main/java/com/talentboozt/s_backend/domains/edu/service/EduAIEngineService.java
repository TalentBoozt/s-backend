package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.LLMTaskType;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EduAIEngineService {

    private final EduAICreditService creditService;
    private final LLMRouter llmRouter;
    private final EduAccessGuardService accessGuard;
    private final com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService;
    private final EduAnalyticsEventService analyticsEventService;

    public EduAIEngineService(EduAICreditService creditService, LLMRouter llmRouter, EduAccessGuardService accessGuard, com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService, EduAnalyticsEventService analyticsEventService) {
        this.creditService = creditService;
        this.llmRouter = llmRouter;
        this.accessGuard = accessGuard;
        this.featureFlagService = featureFlagService;
        this.analyticsEventService = analyticsEventService;
    }

    public String generateCourseOutline(String userId, String courseId, AIGenerationRequest request) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 15;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Generating course outline: userId={}, plan={}, cost={}", userId, plan, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

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

        String aiResponse = llmRouter.generate(plan, LLMTaskType.OUTLINE, systemPrompt, userPrompt, true);

        // POST-GENERATION: deduct credits only after successful LLM call
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_OUTLINE_GENERATION,
                "Generate " + request.getAudienceLevel() + " outline about " + request.getTopic(), aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "COURSE_OUTLINE", "cost", tokenCost));

        log.info("Successfully generated course outline: userId={}, courseId={}", userId, courseId);
        return aiResponse;
    }

    public String generateLessonContent(String userId, String courseId, String lessonObjective) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 30;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Generating lesson content: userId={}, plan={}, cost={}", userId, plan, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = "You are a world-class educator. Write detailed, engaging, and professional lesson content in Markdown format.";
        String userPrompt = String.format(
                "Write a detailed lesson teaching: '%s'. Include core concepts and best practices.", lessonObjective);

        String aiResponse = llmRouter.generate(plan, LLMTaskType.LESSON, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_CONTENT_GENERATION,
                "Write a dynamic long-form article teaching: " + lessonObjective, aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "LESSON_CONTENT", "cost", tokenCost));

        log.info("Successfully generated lesson content: userId={}, courseId={}", userId, courseId);
        return aiResponse;
    }

    public String generateSystemQuiz(String userId, String courseId, String topic) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 10;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Generating quiz: userId={}, plan={}, cost={}", userId, plan, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = """
                You are an educational quiz generator.
                Respond ONLY with a valid JSON array of objects following this structure:
                [
                  {"question": "Question text", "options": ["Option A", "Option B", "Option C"], "answer": "Option B"}
                ]
                """;

        String userPrompt = String.format("Create a 5-question multiple choice quiz on the topic: '%s'.", topic);

        String aiResponse = llmRouter.generate(plan, LLMTaskType.QUIZ, systemPrompt, userPrompt, true);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_QUIZ_GENERATION,
                "Create quiz on: " + topic, aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "QUIZ_GENERATION", "cost", tokenCost));

        log.info("Successfully generated quiz: userId={}, courseId={}", userId, courseId);
        return aiResponse;
    }

    public String generateCourseSummary(String userId, String courseId, String courseContext) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 10;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Generating course summary: userId={}, plan={}, cost={}", userId, plan, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = "You are a professional educational copywriter. Write a compelling, concise course summary and marketing description based on the provided course modules and syllabus.";
        String userPrompt = "Course syllabus context:\n" + courseContext;

        String aiResponse = llmRouter.generate(plan, LLMTaskType.SUMMARY, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_SUMMARY_GENERATION,
                "Generate course summary description", aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "COURSE_SUMMARY", "cost", tokenCost));

        log.info("Successfully generated course summary: userId={}, courseId={}", userId, courseId);
        return aiResponse;
    }

    public String translateCourseContent(String userId, String courseId, String content, String language) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 15;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Translating course content: userId={}, plan={}, language={}, cost={}", userId, plan, language, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = "You are an expert technical translator. Translate the given lesson content exactly into " + language + ", maintaining markdown formatting, technical accuracy, and tone. Do NOT add any extra conversational text.";
        String userPrompt = "Translate this content:\n" + content;

        String aiResponse = llmRouter.generate(plan, LLMTaskType.TRANSLATION, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_TRANSLATION,
                "Translate lesson to " + language, aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "TRANSLATION", "language", language, "cost", tokenCost));

        log.info("Successfully translated course content: userId={}, courseId={}, language={}", userId, courseId, language);
        return aiResponse;
    }

    public String rewriteContent(String userId, String courseId, String content, String style) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 15;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Rewriting course content: userId={}, plan={}, style={}, cost={}", userId, plan, style, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = "You are an expert editor. Rewrite the following lesson content to make it " + style + ". Maintain the core learning objectives, markdown formatting, and technical accuracy.";
        String userPrompt = "Rewrite this content:\n" + content;

        String aiResponse = llmRouter.generate(plan, LLMTaskType.REWRITE, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_REWRITE,
                "Rewrite lesson content to " + style, aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "REWRITE", "style", style, "cost", tokenCost));

        log.info("Successfully rewrote course content: userId={}, courseId={}, style={}", userId, courseId, style);
        return aiResponse;
    }

    public String reviseContent(String userId, String courseId, String content) {
        if (!featureFlagService.isFeatureEnabled(userId, "AI_GENERATION")) {
            log.warn("Feature AI_GENERATION disabled for user: {}", userId);
            throw new EduAccessDeniedException("AI Generation is not available for your current plan.");
        }
        int tokenCost = 10;
        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();

        log.debug("Revising course content: userId={}, plan={}, cost={}", userId, plan, tokenCost);

        // PRE-VALIDATE: check rate limits + balance BEFORE expensive LLM call
        creditService.preValidate(userId, tokenCost, plan);

        String systemPrompt = "You are an expert technical editor. Proofread and revise the following lesson content. Fix any grammatical errors, improve clarity and flow, and maintain markdown formatting. Return the polished content directly.";
        String userPrompt = "Revise this content:\n" + content;

        String aiResponse = llmRouter.generate(plan, LLMTaskType.REVISION, systemPrompt, userPrompt, false);

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_REVISION,
                "Revise and proofread lesson content", aiResponse);

        analyticsEventService.recordEvent(EAnalyticsEvent.AI_USAGE, userId, courseId, Map.of("task", "REVISION", "cost", tokenCost));

        log.info("Successfully revised course content: userId={}, courseId={}", userId, courseId);
        return aiResponse;
    }
}
