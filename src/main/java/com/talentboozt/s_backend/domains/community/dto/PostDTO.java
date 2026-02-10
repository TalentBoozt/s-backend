package com.talentboozt.s_backend.domains.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String id;
    private String authorId;
    private String communityId;
    private String type;
    private PostContentDTO content;
    private PostMetricsDTO metrics;
    private List<ReactionDTO> reactions;
    private List<String> mentionIds;
    private String quotedPostId;
    private String timestamp;
    private String updatedAt;
    private double trendingScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostContentDTO {
        private String title;
        private String text;
        private String url;
        private LinkPreviewDTO linkPreview;
        private List<String> media;
        private List<String> tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkPreviewDTO {
        private String title;
        private String description;
        private String image;
        private String siteName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostMetricsDTO {
        private int upvotes;
        private int downvotes;
        private int comments;
        private int shares;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionDTO {
        private String emoji;
        private int count;
        private boolean userReacted;
        private List<String> userIds;
    }
}
