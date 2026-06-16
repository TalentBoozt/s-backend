package com.talentboozt.s_backend.shared.ai.tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class CareerPathRequest {
    private String education;
    private List<String> skills;
    private List<String> interests;
}
