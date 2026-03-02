package com.talentboozt.s_backend.domains.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleEvaluationDTO {
    private Scores scores;
    private String category;
    private Flags flags;
    private Decision decision;
    private Analysis analysis;
    private Meta meta;

    @Data
    public static class Scores {
        private int contentQuality;
        private int informationalValue;
        private int originality;
        private int engagementPotential;
        private int safetyCompliance;
    }

    @Data
    public static class Flags {
        private boolean markAsHighValue;
        private boolean markAsInformative;
        private boolean manualReviewRequired;
    }

    @Data
    public static class Decision {
        private String publishStatus;
        private int confidenceScore;
    }

    @Data
    public static class Analysis {
        private List<String> strengths;
        private List<String> weaknesses;
        private List<String> policyConcerns;
        private List<String> improvementSuggestions;
    }

    @Data
    public static class Meta {
        private String evaluationVersion;
        private String evaluationTimestamp;
    }
}
