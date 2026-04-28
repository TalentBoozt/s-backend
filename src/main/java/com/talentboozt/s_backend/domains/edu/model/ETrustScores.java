package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_trust_scores")
public class ETrustScores {
    @Id
    private String id;

    @Indexed(unique = true)
    private String creatorId;

    private Double currentScore;
    private Double previousScore;

    // Core Metrics (for calculation)
    private Double averageRating;
    private Integer totalReviews;
    private Double completionRate;
    private Double refundRate;

    private String currentTier;  // BRONZE, SILVER, GOLD, PLATINUM
    private String previousTier;

    private Instant lastCalculatedAt;
    private Instant tierChangedAt;

    // Score breakdown for transparency
    private Double validationScore;
    private Double ratingScore;
    private Double completionScore;
    private Double refundHealthScore;
    private Double reportHealthScore;
    private Double activityScore;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
