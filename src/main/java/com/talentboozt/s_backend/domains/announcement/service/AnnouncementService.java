package com.talentboozt.s_backend.domains.announcement.service;

import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementRequest;
import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementResponse;
import com.talentboozt.s_backend.domains.announcement.event.AnnouncementPublishedEvent;
import com.talentboozt.s_backend.domains.announcement.model.*;
import com.talentboozt.s_backend.domains.announcement.repository.mongodb.AnnouncementRepository;
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
                .status(request.getStatus() != null ? request.getStatus() : AnnouncementStatus.DRAFT)
                .type(request.getType())
                .visibility(request.getVisibility())
                .priority(request.getPriority())
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .createdBy(adminId)
                .pinned(request.isPinned())
                .generateSummary(request.isGenerateSummary())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Announcement saved = announcementRepository.save(announcement);

        if (saved.getStatus() == AnnouncementStatus.PUBLISHED) {
            eventPublisher.publishEvent(new AnnouncementPublishedEvent(this, saved));
        }

        return mapToResponse(saved);
    }

    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AnnouncementResponse updateAnnouncement(String id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setTitle(request.getTitle());
        announcement.setSlug(generateSlug(request.getTitle()));
        announcement.setSummary(request.getSummary());
        announcement.setContent(request.getContent());
        announcement.setCoverImage(request.getCoverImage());
        announcement.setStatus(request.getStatus() != null ? request.getStatus() : announcement.getStatus());
        announcement.setType(request.getType());
        announcement.setVisibility(request.getVisibility());
        announcement.setPriority(request.getPriority());
        announcement.setPublishedAt(request.getPublishedAt());
        announcement.setExpiresAt(request.getExpiresAt());
        announcement.setPinned(request.isPinned());
        announcement.setUpdatedAt(LocalDateTime.now());

        Announcement saved = announcementRepository.save(announcement);

        if (saved.getStatus() == AnnouncementStatus.PUBLISHED) {
            eventPublisher.publishEvent(new AnnouncementPublishedEvent(this, saved));
        }

        return mapToResponse(saved);
    }

    public void deleteAnnouncement(String id) {
        announcementRepository.deleteById(id);
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

    public AnnouncementResponse getById(String id) {
        return announcementRepository.findById(id)
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
