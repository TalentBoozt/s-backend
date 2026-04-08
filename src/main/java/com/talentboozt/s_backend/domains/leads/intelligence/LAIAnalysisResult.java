package com.talentboozt.s_backend.domains.leads.intelligence;

import lombok.Data;

import java.util.List;

@Data
public class LAIAnalysisResult {
    // intent: LEARNING, BUYING, PROBLEM_SOLVING, NOISE
    private String intent;
    // Weights from 0 to 100
    private int intentWeight;
    private int engagementWeight;
    private int recencyWeight;
    // Extracted tags like "resume", "career"
    private List<String> tags;
}
