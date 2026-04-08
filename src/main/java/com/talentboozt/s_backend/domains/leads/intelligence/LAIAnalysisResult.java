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
    
    // New fields for refined scoring
    private double sentiment; // -1.0 (very negative) to 1.0 (very positive)
    private int urgency; // 0 to 100
    
    // Extracted tags
    private List<String> tags;
}
