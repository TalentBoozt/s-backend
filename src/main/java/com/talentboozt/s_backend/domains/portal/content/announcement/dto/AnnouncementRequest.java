package com.talentboozt.s_backend.domains.portal.content.announcement.dto;

import com.talentboozt.s_backend.domains.portal.content.announcement.model.AnnouncementPriority;
import com.talentboozt.s_backend.domains.portal.content.announcement.model.AnnouncementStatus;
import com.talentboozt.s_backend.domains.portal.content.announcement.model.AnnouncementType;
import com.talentboozt.s_backend.domains.portal.content.announcement.model.AnnouncementVisibility;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementRequest {
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private AnnouncementStatus status;
    private AnnouncementType type;
    private AnnouncementVisibility visibility;
    private AnnouncementPriority priority;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private boolean pinned;
    private boolean generateSummary;
}
