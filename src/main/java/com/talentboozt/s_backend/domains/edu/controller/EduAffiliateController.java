package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EAffiliateLinks;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.service.EduAffiliateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/affiliates")
@RequiredArgsConstructor
public class EduAffiliateController {

    private final EduAffiliateService affiliateService;

    @PostMapping("/register")
    public ResponseEntity<EAffiliates> register(@RequestParam String userId) {
        return ResponseEntity.ok(affiliateService.registerAffiliate(userId));
    }

    @PostMapping("/links")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MARKETER')")
    public ResponseEntity<EAffiliateLinks> generateLink(@RequestParam String affiliateId,
            @RequestParam String courseId) {
        return ResponseEntity.ok(affiliateService.generateAffiliateLink(affiliateId, courseId));
    }

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getStats(@com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser String userId) {
        return ResponseEntity.ok(affiliateService.getAffiliateStats(userId));
    }

    @PostMapping("/track/{trackingCode}")
    public ResponseEntity<Void> trackClick(@PathVariable String trackingCode) {
        affiliateService.trackAffiliateClick(trackingCode, null, null);
        return ResponseEntity.ok().build();
    }
}
