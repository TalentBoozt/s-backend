package com.talentboozt.s_backend.shared.ai.tool.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiGeneratedSummary {
    private String summary;
    private List<String> highlights;
    private String snippet;
    private String seoDescription;
}
