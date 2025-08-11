package com.talentboozt.s_backend.domains.ai_tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoadmapResponse {
    private List<String> educationPaths;
    private List<String> skillsToDevelop;
    private List<String> qualifications;
    private List<String> immediateSteps;
}
