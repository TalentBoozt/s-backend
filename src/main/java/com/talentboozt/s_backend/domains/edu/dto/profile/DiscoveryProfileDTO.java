package com.talentboozt.s_backend.domains.edu.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryProfileDTO {
    private PublicProfileDTO personalProfile;
    private EnterprisePortfolioDTO enterpriseProfile;
    private String type; // PERSONAL or ENTERPRISE
}
