package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
@CompoundIndexes({
    @CompoundIndex(name = "community_timestamp", def = "{'communityId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "author_timestamp", def = "{'authorId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "timestamp_idx", def = "{'timestamp': -1}"),
        @CompoundIndex(name = "trending_idx", def = "{'trendingScore': -1}")
})
public class Post {
    @Id
    private String id;
    private String authorId;
    private String communityId;
    private List<String> mentionIds;
    private String quotedPostId;
    private String type; // text, image, link
    private PostContent content;
    private PostMetrics metrics;
    private List<Reaction> reactions;
    private LocalDateTime timestamp;
    private LocalDateTime updatedAt;

    @Builder.Default
    private double trendingScore = 0.0;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostContent {
        private String title;
        private String text;
        private String url;
        private LinkPreview linkPreview;
        private List<String> media;
        private List<String> tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkPreview {
        private String title;
        private String description;
        private String image;
        private String siteName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostMetrics {
        private int upvotes;
        private int downvotes;
        private int comments;
        private int shares;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        private String emoji;
        private int count;
        private List<String> userIds;
    }
}
