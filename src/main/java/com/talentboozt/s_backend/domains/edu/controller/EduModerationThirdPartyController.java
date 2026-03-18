package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.moderation.ReportRequest;
import com.talentboozt.s_backend.domains.edu.dto.thirdparty.GiftRequest;
import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.model.EGifts;
import com.talentboozt.s_backend.domains.edu.model.EReports;
import com.talentboozt.s_backend.domains.edu.service.EduAffiliateService;
import com.talentboozt.s_backend.domains.edu.service.EduGiftService;
import com.talentboozt.s_backend.domains.edu.service.EduModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/trust")
public class EduModerationThirdPartyController {

    private final EduModerationService moderationService;
    private final EduGiftService giftService;
    private final EduAffiliateService affiliateService;

    public EduModerationThirdPartyController(EduModerationService moderationService, 
                                             EduGiftService giftService, 
                                             EduAffiliateService affiliateService) {
        this.moderationService = moderationService;
        this.giftService = giftService;
        this.affiliateService = affiliateService;
    }

    // MODERATION
    @PostMapping("/reports")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EReports> submitReport(@RequestBody ReportRequest request) {
        return ResponseEntity.ok(moderationService.submitReport(request));
    }

    @GetMapping("/reports/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<EReports>> getPendingReports() {
        return ResponseEntity.ok(moderationService.getPendingReports());
    }

    @PutMapping("/reports/{reportId}/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EReports> resolveReport(
            @PathVariable String reportId,
            @RequestParam String adminId,
            @RequestParam EReportStatus status,
            @RequestParam String notes) {
        return ResponseEntity.ok(moderationService.resolveReport(reportId, adminId, status, notes));
    }

    // GIFTS
    @PostMapping("/gifts/send")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EGifts> sendGift(@RequestBody GiftRequest request) {
        return ResponseEntity.ok(giftService.sendGift(request));
    }

    @PostMapping("/gifts/redeem")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EGifts> redeemGift(
            @RequestParam String userId,
            @RequestParam String redeemCode) {
        return ResponseEntity.ok(giftService.redeemGift(userId, redeemCode));
    }

    // AFFILIATES
    @PostMapping("/affiliates/register/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('CREATOR') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EAffiliates> registerAffiliate(@PathVariable String userId) {
        return ResponseEntity.ok(affiliateService.registerAffiliate(userId));
    }
}
