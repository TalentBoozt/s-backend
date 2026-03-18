package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;
import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import com.talentboozt.s_backend.domains.edu.service.EduFinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/finance")
public class EduFinanceController {

    private final EduFinanceService financeService;

    public EduFinanceController(EduFinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/revenue/{creatorId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(@PathVariable String creatorId) {
        return ResponseEntity.ok(financeService.getRevenueSummary(creatorId));
    }

    @PostMapping("/payout/request")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EPayouts> requestPayout(
            @RequestParam String creatorId,
            @RequestBody PayoutRequest request) {
        return ResponseEntity.ok(financeService.requestPayout(creatorId, request));
    }

    @GetMapping("/payout/history/{creatorId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<EPayouts>> getPayoutHistory(@PathVariable String creatorId) {
        return ResponseEntity.ok(financeService.getPayoutHistory(creatorId));
    }

    // Only internal admins should be capable of marking payouts completed
    @PutMapping("/payout/{payoutId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EPayouts> updatePayoutStatus(
            @PathVariable String payoutId,
            @RequestParam EPayoutStatus status) {
        return ResponseEntity.ok(financeService.updatePayoutStatus(payoutId, status));
    }
}
