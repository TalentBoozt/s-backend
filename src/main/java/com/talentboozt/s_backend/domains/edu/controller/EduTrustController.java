package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ETrustScores;
import com.talentboozt.s_backend.domains.edu.service.EduTrustScoreService;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/trust")
@RequiredArgsConstructor
public class EduTrustController {

    private final EduTrustScoreService trustScoreService;
    private final SecurityUtils securityUtils;

    @GetMapping("/my-score")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ETrustScores> getMyScore() {
        String creatorId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(trustScoreService.getTrustScore(creatorId));
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ETrustScores> getCreatorScore(@PathVariable String creatorId) {
        return ResponseEntity.ok(trustScoreService.getTrustScore(creatorId));
    }

    @PostMapping("/recalculate")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> recalculateAll() {
        trustScoreService.recalculateAllTrustScores();
        return ResponseEntity.noContent().build();
    }
}
