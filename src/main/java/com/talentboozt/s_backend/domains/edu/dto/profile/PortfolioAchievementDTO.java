package com.talentboozt.s_backend.domains.edu.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAchievementDTO {
    private String id;
    private String type; // CERTIFICATE, BADGE, MILESTONE
    private String title;
    private String issuerName;
    private Instant issuedAt;
    private String verificationUrl;
}
