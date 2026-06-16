package com.talentboozt.s_backend.shared.ai.tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class CareerPathResponse {
    private List<String> careerPaths;
    private List<String> upskillingOptions;
    private List<String> jobRoles;
    private List<String> courseKeywords;
}
