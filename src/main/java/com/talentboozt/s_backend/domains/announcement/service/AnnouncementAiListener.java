package com.talentboozt.s_backend.domains.announcement.service;

import com.talentboozt.s_backend.domains.ai_tool.dto.AiGeneratedSummary;
import com.talentboozt.s_backend.domains.ai_tool.service.AiService;
import com.talentboozt.s_backend.domains.announcement.event.AnnouncementPublishedEvent;
import com.talentboozt.s_backend.domains.announcement.model.Announcement;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementType;
import com.talentboozt.s_backend.domains.announcement.repository.mongodb.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementAiListener {
    private final AiService aiService;
    private final AnnouncementRepository announcementRepository;

    @Async
    @EventListener
    public void handleAnnouncementPublished(AnnouncementPublishedEvent event) {
        Announcement announcement = event.getAnnouncement();

        boolean shouldGenerate = announcement.isGenerateSummary() ||
                announcement.getType() == AnnouncementType.FEATURE_RELEASE;

        if (shouldGenerate) {
            try {
                log.info("Generating AI summary for announcement: {}", announcement.getId());
                AiGeneratedSummary summary = aiService.generateReleaseSummary(announcement.getContent());

                announcement.setAiSummary(summary.getSummary());
                announcement.setAiHighlights(summary.getHighlights());
                announcement.setAiSnippet(summary.getSnippet());
                announcement.setAiSeoDescription(summary.getSeoDescription());

                announcementRepository.save(announcement);
                log.info("AI summary generated and saved for announcement: {}", announcement.getId());
            } catch (Exception e) {
                log.error("Failed to generate AI summary for announcement: {}", announcement.getId(), e);
            }
        }
    }
}
