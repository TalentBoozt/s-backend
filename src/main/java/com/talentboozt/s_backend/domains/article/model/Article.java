package com.talentboozt.s_backend.domains.article.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class Article {
    @Id
    private String id;

    @TextIndexed
    private String title;

    @Indexed(unique = true)
    private String slug;

    @TextIndexed
    private String content; // Markdown

    @TextIndexed
    private String excerpt;

    private String authorId;
    private String coverImage;

    private List<String> tagIds;

    private ArticleStatus status;
    private int readTime; // in minutes

    @Builder.Default
    private long views = 0;

    @Builder.Default
    private long likes = 0;

    private boolean featured;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // AI Generated Fields
    private String aiSummary;
    private List<String> aiHighlights;
    private String aiSnippet;
    private String aiSeoDescription;
}
