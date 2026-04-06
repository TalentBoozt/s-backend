package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;
import com.talentboozt.s_backend.domains.edu.model.ECreatorFinanceSettings;
import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import com.talentboozt.s_backend.domains.edu.model.ERefund;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.service.EduFinanceService;
import com.talentboozt.s_backend.domains.edu.service.EduRefundService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/edu/finance")
public class EduFinanceController {

    private final EduFinanceService financeService;
    private final EduRefundService refundService;
    private final com.talentboozt.s_backend.domains.edu.service.StripeConnectService stripeConnectService;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EPayoutScheduleRepository scheduleRepository;

    public EduFinanceController(EduFinanceService financeService, EduRefundService refundService,
                                com.talentboozt.s_backend.domains.edu.service.StripeConnectService stripeConnectService,
                                com.talentboozt.s_backend.domains.edu.repository.mongodb.EPayoutScheduleRepository scheduleRepository) {
        this.financeService = financeService;
        this.refundService = refundService;
        this.stripeConnectService = stripeConnectService;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/ledger")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<EHoldingLedger>> getFinanceLedger(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getFinanceLedger(creatorId));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getRevenueSummary(creatorId));
    }

    @PostMapping("/payout/request")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EPayouts> requestPayout(
            @AuthenticatedUser String creatorId,
            @Valid @RequestBody PayoutRequest request) {
        return ResponseEntity.ok(financeService.requestPayout(creatorId, request));
    }

    @GetMapping("/payout/history")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<EPayouts>> getPayoutHistory(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getPayoutHistory(creatorId));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<ETransactions>> getCreatorTransactions(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(financeService.getCreatorTransactions(creatorId));
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<ECreatorFinanceSettings> getFinanceSettings(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(financeService.getFinanceSettings(userId));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<ECreatorFinanceSettings> updateFinanceSettings(@AuthenticatedUser String userId,
            @RequestBody ECreatorFinanceSettings settings) {
        return ResponseEntity.ok(financeService.updateFinanceSettings(userId, settings));
    }

    // Only internal admins should be capable of marking payouts completed
    @PutMapping("/payout/{payoutId}/status")
    @PreAuthorize("hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<EPayouts> updatePayoutStatus(
            @PathVariable String payoutId,
            @RequestParam EPayoutStatus status) throws Exception {
        return ResponseEntity.ok(financeService.updatePayoutStatus(payoutId, status));
    }

    @PostMapping("/stripe/onboard")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<Map<String, String>> stripeOnboard(@AuthenticatedUser String userId, @RequestBody Map<String, String> body) throws Exception {
        ECreatorFinanceSettings settings = financeService.getFinanceSettings(userId);
        String accountId = settings.getStripeAccountId();
        if (accountId == null) {
            String email = body.get("email");
            if (email == null) email = "creator@example.com"; // Fallback if not provided
            accountId = stripeConnectService.createStripeAccount(userId, email);
        }
        String link = stripeConnectService.createOnboardingLink(accountId);
        return ResponseEntity.ok(Map.of("url", link));
    }
    
    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<com.talentboozt.s_backend.domains.edu.model.EPayoutSchedule> updateSchedule(
            @AuthenticatedUser String userId,
            @RequestBody com.talentboozt.s_backend.domains.edu.model.EPayoutSchedule schedule) {
        schedule.setCreatorId(userId);
        schedule.setNextScheduledAt(java.time.Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS)); // Default
        return ResponseEntity.ok(scheduleRepository.save(schedule));
    }
    
    @GetMapping("/schedule")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<com.talentboozt.s_backend.domains.edu.model.EPayoutSchedule> getSchedule(@AuthenticatedUser String userId) {
        return scheduleRepository.findByCreatorId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Refund Endpoints ──────────────────────────────────────────

    /** Admin: initiate a refund for a specific transaction */
    @PostMapping("/refund/{transactionId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<ERefund> initiateRefund(
            @PathVariable String transactionId,
            @RequestBody Map<String, Object> body,
            @AuthenticatedUser String adminId) {
        Double amount = body.get("amount") != null ? ((Number) body.get("amount")).doubleValue() : null;
        String reason = body.get("reason") != null ? body.get("reason").toString() : null;
        return ResponseEntity.ok(refundService.initiateRefund(transactionId, amount, reason, adminId));
    }

    /** Buyer: view their refund history */
    @GetMapping("/refunds")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<ERefund>> getMyRefunds(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(refundService.getRefundsByBuyer(userId));
    }

    /** Creator: view refunds affecting their courses */
    @GetMapping("/refunds/creator")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<ERefund>> getCreatorRefunds(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(refundService.getRefundsBySeller(creatorId));
    }
}
