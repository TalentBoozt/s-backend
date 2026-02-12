package com.talentboozt.s_backend.domains.announcement.dto;

import com.talentboozt.s_backend.domains.announcement.model.AnnouncementPriority;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementType;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementVisibility;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private String id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private AnnouncementType type;
    private AnnouncementVisibility visibility;
    private AnnouncementPriority priority;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private String createdBy;
    private boolean pinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
