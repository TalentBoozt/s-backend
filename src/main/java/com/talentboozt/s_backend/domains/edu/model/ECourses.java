package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;

import com.talentboozt.s_backend.domains.edu.enums.ECourseContentType;
import com.talentboozt.s_backend.domains.edu.enums.ECourseLevel;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.enums.ECourseType;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_courses")
@CompoundIndexes({
    @CompoundIndex(name = "workspace_creator_idx", def = "{'workspaceId': 1, 'creatorId': 1}")
})
public class ECourses {
    @Id
    private String id;
    
    @Indexed
    private String workspaceId;
    
    @Indexed
    private String creatorId;
    
    @TextIndexed
    private String title;
    
    @TextIndexed
    private String description;
    
    private String shortDescription;
    private String thumbnail;
    private String previewVideoUrl;
    
    @Indexed
    private ECourseType type;
    
    @Indexed
    private ECourseContentType contentType;
    
    private Double price;
    private Double compareAtPrice;
    private String currency;
    
    private Boolean published;
    private Boolean isPrivate;
    
    @Indexed
    private ECourseLevel level;
    private String language;
    
    private String[] tags;
    private String[] categories;
    private String[] subCategories;
    
    @TextIndexed
    private String[] keywords;
    private String[] skills;
    private String[] prerequisites;
    private String[] outcomes;
    private String[] sections; // List of section IDs
    
    @Indexed(unique = true)
    private String slug;
    
    @Indexed
    private Double rating;
    
    @Indexed
    private Integer totalEnrollments;
    private Integer totalReviews;
    private Integer totalHours;
    private Integer totalLessons;
    
    private Boolean isFeatured;
    private Boolean isTrending;
    
    @Indexed
    private Integer searchRank;
    
    @Indexed
    private ECourseStatus status;
    
    private Boolean aiGenerated;
    private ECourseValidationStatus validationStatus;
    private Double aiScore;
    private Boolean talnovaVerified;

    /** Set when moderation rejects a submission (shown to creator). */
    private String moderationRejectionReason;
    
    @Transient
    private String trustDisclaimer;

    @Transient
    private String creatorTier;

    @Transient
    private String trustWarning;
    
    private Instant publishedAt;
    private String createdBy;
    private String updatedBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
