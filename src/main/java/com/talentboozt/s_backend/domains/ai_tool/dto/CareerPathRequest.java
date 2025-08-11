package com.talentboozt.s_backend.domains.ai_tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class CareerPathRequest {
    private String education;
    private List<String> skills;
    private List<String> interests;
}
