package com.talentboozt.s_backend.domains.announcement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "announcements")
public class Announcement {
    @Id
    private String id;
    private String title;

    @Indexed(unique = true)
    private String slug;

    private String summary;
    private String content; // Markdown
    private String coverImage;

    private AnnouncementType type;
    private AnnouncementVisibility visibility;
    private AnnouncementPriority priority;

    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;

    private String createdBy; // Admin User ID
    private boolean pinned;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
