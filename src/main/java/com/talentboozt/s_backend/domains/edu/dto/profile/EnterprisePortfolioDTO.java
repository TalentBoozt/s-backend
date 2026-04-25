package com.talentboozt.s_backend.domains.edu.dto.profile;

import com.talentboozt.s_backend.domains.edu.enums.EWorkspaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePortfolioDTO {
    private String workspaceId;
    private String name;
    private String logoUrl;
    private String description;
    private EWorkspaceType type;
    
    private List<PortfolioCourseDTO> offeredPrograms;
    private List<String> keyInstructorIds; // Linked to their personal portfolios
    
    private Map<String, Object> metrics;
}
