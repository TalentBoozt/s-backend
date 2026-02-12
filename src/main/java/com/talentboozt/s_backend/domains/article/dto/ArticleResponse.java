package com.talentboozt.s_backend.domains.article.dto;

import com.talentboozt.s_backend.domains.article.model.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleResponse {
    private String id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String authorId;
    private String coverImage;
    private List<TagResponse> tags;
    private ArticleStatus status;
    private int readTime;
    private long views;
    private long likes;
    private boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // AI Generated Fields
    private String aiSummary;
    private List<String> aiHighlights;
    private String aiSnippet;
    private String aiSeoDescription;
}
