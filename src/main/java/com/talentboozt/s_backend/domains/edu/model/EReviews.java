package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
@Document(collection = "edu_reviews")
@CompoundIndexes({
        @CompoundIndex(name = "user_course_review_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
})
public class EReviews {
    @Id
    private String id;

    @Indexed
    private String courseId;

    @Indexed
    private String userId;

    @Indexed
    private Double rating;

    private String title;
    private String content;

    private Boolean isVerifiedPurchase = true;
    private Integer helpfulVotes = 0;
    private Boolean isReported = false;

    @Indexed
    private Boolean isVisible = true;

    private String createdBy;
    private String updatedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
