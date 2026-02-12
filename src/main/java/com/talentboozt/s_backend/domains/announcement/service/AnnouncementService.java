package com.talentboozt.s_backend.domains.announcement.service;

import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementRequest;
import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementResponse;
import com.talentboozt.s_backend.domains.announcement.event.AnnouncementPublishedEvent;
import com.talentboozt.s_backend.domains.announcement.model.*;
import com.talentboozt.s_backend.domains.announcement.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request, String adminId) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .summary(request.getSummary())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .type(request.getType())
                .visibility(request.getVisibility())
                .priority(request.getPriority())
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .createdBy(adminId)
                .pinned(request.isPinned())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Announcement saved = announcementRepository.save(announcement);
        eventPublisher.publishEvent(new AnnouncementPublishedEvent(this, saved));
        return mapToResponse(saved);
    }

    public List<AnnouncementResponse> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements(LocalDateTime.now())
                .stream()
                .sorted((a, b) -> {
                    if (a.isPinned() && !b.isPinned())
                        return -1;
                    if (!a.isPinned() && b.isPinned())
                        return 1;
                    return b.getPublishedAt().compareTo(a.getPublishedAt());
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AnnouncementResponse getBySlug(String slug) {
        return announcementRepository.findBySlug(slug)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setSlug(announcement.getSlug());
        response.setSummary(announcement.getSummary());
        response.setContent(announcement.getContent());
        response.setCoverImage(announcement.getCoverImage());
        response.setType(announcement.getType());
        response.setVisibility(announcement.getVisibility());
        response.setPriority(announcement.getPriority());
        response.setPublishedAt(announcement.getPublishedAt());
        response.setExpiresAt(announcement.getExpiresAt());
        response.setCreatedBy(announcement.getCreatedBy());
        response.setPinned(announcement.isPinned());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setUpdatedAt(announcement.getUpdatedAt());
        return response;
    }
}
