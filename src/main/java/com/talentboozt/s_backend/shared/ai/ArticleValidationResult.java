package com.talentboozt.s_backend.shared.ai;

import java.util.Map;
import java.util.List;

public record ArticleValidationResult(
        int qualityScore, // 0–100
        boolean isValid,
        List<String> issues, // e.g. ["Too short", "Clickbait title"]
        String summary // optional short summary
) {
    /**
     * Returns the JSON Schema representation for Gemini's responseSchema.
     */
    public static Map<String, Object> getSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "qualityScore", Map.of("type", "integer", "description", "Score from 0 to 100"),
                        "isValid", Map.of("type", "boolean", "description", "True if article meets basic standards"),
                        "issues", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")),
                        "summary", Map.of("type", "string", "description", "A 1-2 sentence summary of findings")),
                "required", List.of("qualityScore", "isValid", "issues", "summary"));
    }
}
