package com.talentboozt.s_backend.domains.resume.service;

import com.talentboozt.s_backend.domains.resume.dto.AiResumeRequest;
import com.talentboozt.s_backend.domains.resume.dto.AiResumeResponse;
import com.talentboozt.s_backend.domains.resume.dto.ResumeSummaryDto;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import com.talentboozt.s_backend.shared.ai.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * AI-assisted content generation for the Resume Builder domain.
 *
 * Rules:
 * - Only authenticated users may call this service (enforced in controller via
 * JWT).
 * - Each resume has a hard cap of MAX_AI_USAGE (5) calls — tracked in
 * ResumeModel.aiUsageCount.
 * - Uses GeminiClient (model rotation + retry) for content generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAiService {

    private final GeminiClient geminiClient;
    private final ResumeService resumeService;

    private static final String SYSTEM_PROMPT = "You are an expert resume writer and ATS optimization specialist. " +
            "Generate professional, concise content tailored for modern job applications. " +
            "Always respond in the exact JSON structure requested. " +
            "Use strong action verbs. Keep content truthful and impactful.";

    /**
     * Main entry point. Validates auth + AI usage limit, then delegates to
     * type-specific generators.
     *
     * @param employeeId SSO user ID from JWT
     * @param req        request body from frontend
     * @return generated content + remaining usage count
     */
    public AiResumeResponse generate(String employeeId, AiResumeRequest req) {
        // ── Guard: resume must belong to this user AND usage limit not exceeded ──
        if (!resumeService.canUseAi(req.getResumeId(), employeeId)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "AI usage limit reached for this resume. Maximum " +
                            ResumeSummaryDto.MAX_AI_USAGE + " generations per resume.");
        }

        // ── Dispatch by type ─────────────────────────────────────────────────
        AiResumeResponse response = switch (req.getType()) {
            case "summary" -> generateSummary(req);
            case "experience" -> improveExperience(req);
            case "skills" -> suggestSkills(req);
            case "education" -> generateEducationDesc(req);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported AI type: " + req.getType());
        };

        // ── Increment counter AFTER successful generation ────────────────────
        ResumeModel updated = resumeService.incrementAiUsage(req.getResumeId(), employeeId);
        response.setAiUsageRemaining(Math.max(0, ResumeSummaryDto.MAX_AI_USAGE - updated.getAiUsageCount()));
        return response;
    }

    // ─── Summary Generation ──────────────────────────────────────────────────

    private AiResumeResponse generateSummary(AiResumeRequest req) {
        String userPrompt = """
                Generate a professional 2-4 sentence resume summary.

                Current summary (if any): %s
                Job description / target role (if provided): %s

                Respond in JSON: { "generatedText": "<the summary>", "note": "<brief tip>" }
                """.formatted(safe(req.getContext()), safe(req.getJobDescription()));

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "generatedText", Map.of("type", "string"),
                        "note", Map.of("type", "string")));

        GeminiClient.AiResponse<Map<String, Object>> raw = geminiClient.callStructuredApiWithRaw(SYSTEM_PROMPT,
                userPrompt, mapType(), schema);

        AiResumeResponse res = new AiResumeResponse();
        res.setType("summary");
        res.setGeneratedText((String) raw.parsed().get("generatedText"));
        res.setNote((String) raw.parsed().get("note"));
        return res;
    }

    // ─── Experience Bullet Points ────────────────────────────────────────────

    private AiResumeResponse improveExperience(AiResumeRequest req) {
        String userPrompt = """
                Improve and expand the following work experience bullet points.
                Return 3-5 strong, ATS-optimized bullet points.

                Current bullets / context: %s
                Target job description (if any): %s

                Respond in JSON: { "bulletPoints": ["...", "..."], "note": "<brief tip>" }
                """.formatted(safe(req.getContext()), safe(req.getJobDescription()));

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "bulletPoints", Map.of("type", "array", "items", Map.of("type", "string")),
                        "note", Map.of("type", "string")));

        GeminiClient.AiResponse<Map<String, Object>> raw = geminiClient.callStructuredApiWithRaw(SYSTEM_PROMPT,
                userPrompt, mapType(), schema);

        AiResumeResponse res = new AiResumeResponse();
        res.setType("experience");
        res.setBulletPoints(castStringList(raw.parsed().get("bulletPoints")));
        res.setNote((String) raw.parsed().get("note"));
        return res;
    }

    // ─── Skills Suggestions ──────────────────────────────────────────────────

    private AiResumeResponse suggestSkills(AiResumeRequest req) {
        String userPrompt = """
                Suggest 8-12 relevant technical and soft skills for the given profile.

                Existing skills: %s
                Target role / job description: %s

                Respond in JSON: { "suggestedSkills": ["Skill1", "Skill2", ...], "note": "<tip>" }
                """.formatted(safe(req.getContext()), safe(req.getJobDescription()));

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "suggestedSkills", Map.of("type", "array", "items", Map.of("type", "string")),
                        "note", Map.of("type", "string")));

        GeminiClient.AiResponse<Map<String, Object>> raw = geminiClient.callStructuredApiWithRaw(SYSTEM_PROMPT,
                userPrompt, mapType(), schema);

        AiResumeResponse res = new AiResumeResponse();
        res.setType("skills");
        res.setSuggestedSkills(castStringList(raw.parsed().get("suggestedSkills")));
        res.setNote((String) raw.parsed().get("note"));
        return res;
    }

    // ─── Education Description ───────────────────────────────────────────────

    private AiResumeResponse generateEducationDesc(AiResumeRequest req) {
        String userPrompt = """
                Write a concise 1-2 sentence description for this educational entry.

                Degree / institution context: %s

                Respond in JSON: { "generatedText": "<description>", "note": "<tip>" }
                """.formatted(safe(req.getContext()));

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "generatedText", Map.of("type", "string"),
                        "note", Map.of("type", "string")));

        GeminiClient.AiResponse<Map<String, Object>> raw = geminiClient.callStructuredApiWithRaw(SYSTEM_PROMPT,
                userPrompt, mapType(), schema);

        AiResumeResponse res = new AiResumeResponse();
        res.setType("education");
        res.setGeneratedText((String) raw.parsed().get("generatedText"));
        res.setNote((String) raw.parsed().get("note"));
        return res;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String safe(String s) {
        return s != null ? s : "";
    }

    @SuppressWarnings("unchecked")
    private List<String> castStringList(Object obj) {
        if (obj instanceof List<?> list)
            return (List<String>) list;
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Class<Map<String, Object>> mapType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }
}
