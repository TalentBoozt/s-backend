package com.talentboozt.s_backend.domains.ai_tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class CareerPathResponse {
    private List<String> careerPaths;
    private List<String> upskillingOptions;
    private List<String> jobRoles;
    private List<String> courseKeywords;
}
