package com.talentboozt.s_backend.domains.resume.service;

import com.talentboozt.s_backend.shared.ai.GeminiClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtsService {

    private final GeminiClient geminiClient;

    private static final String SYSTEM_PROMPT = "You are an AI ATS (Applicant Tracking System) expert. " +
            "Your task is to analyze resume text and provide a compatibility score, identified keywords, and specific improvement suggestions. "
            +
            "Be critical but constructive. Ensure the output is valid JSON.";

    public AtsAnalysisResult analyze(String resumeText, String jobDescription) {
        String jdPart = (jobDescription != null && !jobDescription.isBlank())
                ? "Job Description to match against:\n" + jobDescription + "\n\n"
                : "No specific job description provided. Analyze for general ATS best practices.\n\n";

        String userPrompt = """
                Analyze the following resume text for ATS optimization.

                %s
                Resume Text:
                %s

                Provide:
                1. An overall compatibility score (0-100). If a job description was provided, this should reflect how well the resume matches the JD.
                2. A list of key technical and soft skills (keywords). Indicate which are found in the resume and which are missing (especially those relevant to the JD).
                3. A list of 3-5 specific, actionable improvement suggestions.

                Respond in EXACTLY this JSON structure:
                {
                  "score": 85,
                  "keywords": [
                    {"keyword": "Java", "found": true},
                    {"keyword": "React", "found": false}
                  ],
                  "improvements": [
                    "Suggestion 1",
                    "Suggestion 2"
                  ]
                }
                """
                .formatted(jdPart, resumeText);

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "score", Map.of("type", "integer"),
                        "keywords", Map.of("type", "array", "items", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "keyword", Map.of("type", "string"),
                                        "found", Map.of("type", "boolean")))),
                        "improvements", Map.of("type", "array", "items", Map.of("type", "string"))));

        GeminiClient.AiResponse<Map<String, Object>> response = geminiClient.callStructuredApiWithRaw(
                SYSTEM_PROMPT,
                userPrompt,
                mapType(),
                schema);

        Map<String, Object> data = response.parsed();

        AtsAnalysisResult result = new AtsAnalysisResult();
        result.setScore((Integer) data.get("score"));
        result.setImprovements((List<String>) data.get("improvements"));

        List<Map<String, Object>> keywordsRaw = (List<Map<String, Object>>) data.get("keywords");
        List<KeywordMatch> keywords = keywordsRaw.stream()
                .map(m -> new KeywordMatch((String) m.get("keyword"), (Boolean) m.get("found")))
                .toList();
        result.setKeywords(keywords);

        return result;
    }

    @Data
    public static class AtsAnalysisResult {
        private int score;
        private List<String> improvements;
        private List<KeywordMatch> keywords;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeywordMatch {
        private String keyword;
        private boolean found;
    }

    @SuppressWarnings("unchecked")
    private Class<Map<String, Object>> mapType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }
}
