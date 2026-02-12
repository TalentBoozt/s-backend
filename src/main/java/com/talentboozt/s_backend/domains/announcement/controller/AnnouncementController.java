package com.talentboozt.s_backend.domains.announcement.controller;

import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementRequest;
import com.talentboozt.s_backend.domains.announcement.dto.AnnouncementResponse;
import com.talentboozt.s_backend.domains.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<AnnouncementResponse> create(@RequestBody AnnouncementRequest request,
            @RequestHeader("X-User-Id") String userId) {
        // In a real scenario, roles would be checked here or via @PreAuthorize
        return ResponseEntity.ok(announcementService.createAnnouncement(request, userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementResponse>> getActive() {
        return ResponseEntity.ok(announcementService.getActiveAnnouncements());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<AnnouncementResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(announcementService.getBySlug(slug));
    }
}
