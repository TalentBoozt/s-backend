package com.talentboozt.s_backend.domains.portal.content.article.dto;

import com.talentboozt.s_backend.domains.portal.content.article.model.ArticleStatus;
import lombok.Data;

import java.util.List;

@Data
public class ArticleRequest {
    private String title;
    private String content;
    private String excerpt;
    private String coverImage;
    private List<String> tags; // Tag names
    private ArticleStatus status;
    private boolean featured;
}
