package com.talentboozt.s_backend.domains.edu.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class EWThreadsDTO {
    private String id;
    private String title;
    private String content;
    private String type;
    private String status;
    private String channelId;
    private String[] attachments;
    private String[] reactions;
    private String[] mentions;
    private String[] tags;
    private String[] categories;
    private String[] labels;
    private String[] comments;
    private String[] replies;
    private String[] likes;
    private String[] dislikes;
    private String[] shares;
    private String[] saves;
    private String[] bookmarks;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
