package com.talentboozt.s_backend.domains.ai_tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoadmapResponse {
    private List<String> educationPaths;
    private List<String> skillsToDevelop;
    private List<String> qualifications;
    private List<Experience> requiredExperiencesWithAverageYears;
    private List<String> immediateSteps;
}

@Data
class Experience {
    private String experienceName;
    private int averageYears;
}
