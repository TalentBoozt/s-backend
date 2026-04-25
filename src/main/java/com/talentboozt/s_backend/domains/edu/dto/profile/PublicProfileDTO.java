package com.talentboozt.s_backend.domains.edu.dto.profile;

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
public class PublicProfileDTO {
    private String id;
    private String name;
    private String title;
    private String avatar;
    private String bio;
    private String[] roles;
    private String planShortCode;

    private Map<String, String> socialLinks;
    private String[] skills;
    private String[] experiences;

    private Map<String, Object> stats;

    private List<PortfolioCourseDTO> highlightedContent;
    private List<PortfolioAchievementDTO> achievements;
    private List<PortfolioReviewDTO> topReviews;

    private boolean isProMember;
    private boolean isEnterpriseVerified;
}
