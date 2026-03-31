package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;
import com.talentboozt.s_backend.domains.edu.model.ECreatorFinanceSettings;
import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.service.EduFinanceService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
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

    @GetMapping("/ledger")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<EHoldingLedger>> getFinanceLedger(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getFinanceLedger(creatorId));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getRevenueSummary(creatorId));
    }

    @PostMapping("/payout/request")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EPayouts> requestPayout(
            @AuthenticatedUser String creatorId,
            @Valid @RequestBody PayoutRequest request) {
        return ResponseEntity.ok(financeService.requestPayout(creatorId, request));
    }

    @GetMapping("/payout/history")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<EPayouts>> getPayoutHistory(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getPayoutHistory(creatorId));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<ETransactions>> getCreatorTransactions(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getCreatorTransactions(creatorId));
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECreatorFinanceSettings> getFinanceSettings(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(financeService.getFinanceSettings(userId));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECreatorFinanceSettings> updateFinanceSettings(@AuthenticatedUser String userId, @RequestBody ECreatorFinanceSettings settings) {
        return ResponseEntity.ok(financeService.updateFinanceSettings(userId, settings));
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
