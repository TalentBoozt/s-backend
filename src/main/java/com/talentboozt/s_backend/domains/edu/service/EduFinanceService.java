package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;
import com.talentboozt.s_backend.domains.edu.model.ECreatorFinanceSettings;
import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECreatorFinanceSettingsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EPayoutsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EduFinanceService {

    private final ETransactionsRepository transactionsRepository;
    private final EPayoutsRepository payoutsRepository;
    private final ECreatorFinanceSettingsRepository financeSettingsRepository;
    private final EHoldingLedgerRepository holdingLedgerRepository;
    private final EduCryptoService cryptoService;
    private final EduAuditService auditService;
    private final EduLedgerService ledgerService;
    private final StripeConnectService stripeConnectService;
    private final EduWalletService walletService;

    public EduFinanceService(ETransactionsRepository transactionsRepository,
            EPayoutsRepository payoutsRepository,
            ECreatorFinanceSettingsRepository financeSettingsRepository,
            EHoldingLedgerRepository holdingLedgerRepository,
            EduCryptoService cryptoService,
            EduAuditService auditService,
            EduLedgerService ledgerService,
            StripeConnectService stripeConnectService,
            EduWalletService walletService) {
        this.transactionsRepository = transactionsRepository;
        this.payoutsRepository = payoutsRepository;
        this.financeSettingsRepository = financeSettingsRepository;
        this.holdingLedgerRepository = holdingLedgerRepository;
        this.cryptoService = cryptoService;
        this.auditService = auditService;
        this.ledgerService = ledgerService;
        this.stripeConnectService = stripeConnectService;
        this.walletService = walletService;
    }

    public RevenueSummaryDTO getRevenueSummary(String creatorId) {
        List<ETransactions> transactions = transactionsRepository.findBySellerId(creatorId);
        List<EPayouts> payouts = payoutsRepository.findByCreatorId(creatorId);

        double totalEarnings = transactions.stream()
                .mapToDouble(t -> t.getCreatorEarning() != null ? t.getCreatorEarning() : 0.0)
                .sum();

        List<EHoldingLedger> clearedRecords = holdingLedgerRepository.findByBeneficiaryIdAndStatus(creatorId,
                EHoldingStatus.CLEARED);
        List<EHoldingLedger> heldRecords = holdingLedgerRepository.findByBeneficiaryIdAndStatus(creatorId,
                EHoldingStatus.HELD);
        List<EHoldingLedger> clawbackRecords = holdingLedgerRepository.findByBeneficiaryIdAndStatus(creatorId,
                EHoldingStatus.CLAWBACK);

        double availableEarnings = clearedRecords.stream()
                .mapToDouble(h -> h.getAmount() != null ? h.getAmount() : 0.0)
                .sum();

        double clawbackAmount = clawbackRecords.stream()
                .mapToDouble(h -> h.getAmount() != null ? h.getAmount() : 0.0)
                .sum();

        availableEarnings += clawbackAmount; // clawback amount is negative so addition reduces balance

        double pendingClearance = heldRecords.stream()
                .mapToDouble(h -> h.getAmount() != null ? h.getAmount() : 0.0)
                .sum();

        double withdrawnAmount = payouts.stream()
                .filter(p -> p.getStatus() == EPayoutStatus.COMPLETED)
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        double requestedAmount = payouts.stream()
                .filter(p -> p.getStatus() == EPayoutStatus.REQUESTED || p.getStatus() == EPayoutStatus.PROCESSING)
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        double availableBalance = Math.max(0.0, availableEarnings - (withdrawnAmount + requestedAmount));
        double pendingBalance = totalEarnings - (withdrawnAmount + requestedAmount);

        return RevenueSummaryDTO.builder()
                .creatorId(creatorId)
                .totalEarnings(totalEarnings)
                .withdrawnAmount(withdrawnAmount)
                .pendingBalance(pendingBalance)
                .availableBalance(availableBalance)
                .pendingClearance(pendingClearance)
                .totalTransactions(transactions.size())
                .build();
    }

    public EPayouts requestPayout(String creatorId, PayoutRequest request) {
        RevenueSummaryDTO summary = getRevenueSummary(creatorId);

        if (request.getAmount() < 25) {
            throw new EduBadRequestException("Minimum payout is $25");
        }

        ECreatorFinanceSettings settings = getFinanceSettings(creatorId);
        if (!"VERIFIED".equals(settings.getProfileVerificationStatus()) ||
                !"VERIFIED".equals(settings.getTaxVerificationStatus())) {
            throw new EduBadRequestException("Payouts disabled: Identity or tax features are unverified.");
        }

        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        if (payoutsRepository.countByCreatorIdAndRequestedAtAfter(creatorId, weekAgo) >= 2) {
            throw new EduBadRequestException("Maximum 2 payout requests allowed per week.");
        }

        if (request.getAmount() > summary.getAvailableBalance()) {
            throw new EduBadRequestException(
                    "Insufficient cleared balance for this payout request. Some funds may still be pending clearance (14-day period).");
        }

        EPayouts payout = EPayouts.builder()
                .creatorId(creatorId)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .method(request.getMethod())
                .bankDetails(cryptoService.encrypt(request.getBankDetails()))
                .paypalEmail(request.getPaypalEmail())
                .status(EPayoutStatus.REQUESTED)
                .requestedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        EPayouts saved = payoutsRepository.save(payout);
        walletService.requestPayout(creatorId, request.getAmount(), saved.getId());
        return saved;
    }

    public List<EPayouts> getPayoutHistory(String creatorId) {
        List<EPayouts> history = payoutsRepository.findByCreatorId(creatorId);
        history.forEach(p -> {
            if (p.getBankDetails() != null) {
                try {
                    p.setBankDetails(cryptoService.decrypt(p.getBankDetails()));
                } catch (Exception e) {
                    p.setBankDetails("[ENCRYPTED DATA ERROR]");
                }
            }
        });
        return history;
    }

    public List<ETransactions> getCreatorTransactions(String creatorId) {
        return transactionsRepository.findBySellerId(creatorId);
    }

    public List<EHoldingLedger> getFinanceLedger(String sellerId) {
        return holdingLedgerRepository.findByBeneficiaryIdOrderByCreatedAtDesc(sellerId);
    }

    // Admin Operation - Security Step
    public EPayouts approvePayout(String adminId, String payoutId) {
        EPayouts payout = payoutsRepository.findById(payoutId)
                .orElseThrow(() -> new EduResourceNotFoundException("Payout request not found with id: " + payoutId));

        if (payout.getStatus() != EPayoutStatus.REQUESTED) {
            throw new EduBadRequestException("Payout must be in REQUESTED state to approve");
        }

        payout.setStatus(EPayoutStatus.PROCESSING);
        return payoutsRepository.save(payout);
    }

    // Admin Operation
    public EPayouts updatePayoutStatus(String payoutId, EPayoutStatus status, HttpServletRequest request)
            throws Exception {
        EPayouts payout = payoutsRepository.findById(payoutId)
                .orElseThrow(() -> new EduResourceNotFoundException("Payout request not found with id: " + payoutId));

        EPayoutStatus oldStatus = payout.getStatus();
        payout.setStatus(status);
        if (status == EPayoutStatus.COMPLETED) {
            payout.setPaidAt(Instant.now());
            ledgerService.recordPayout(payout.getId(), payout.getCreatorId(), payout.getAmount(), payout.getCurrency());

            // Execute real Stripe transfer if the method is STRIPE
            if (payout.getMethod() == com.talentboozt.s_backend.domains.edu.enums.EPayoutMethod.STRIPE) {
                ECreatorFinanceSettings settings = getFinanceSettings(payout.getCreatorId());
                if (settings.getStripeAccountId() != null) {
                    stripeConnectService.transferFunds(settings.getStripeAccountId(), payout.getAmount(),
                            payout.getCurrency(), payout.getId());
                } else {
                    throw new EduBadRequestException("Creator does not have a Stripe Connect account configured.");
                }
            }
        }

        EPayouts saved = payoutsRepository.save(payout);

        // Audit Log
        auditService.logAction(
                "SYSTEM_ADMIN", // Ideally passed from controller
                "UPDATE_PAYOUT_STATUS",
                payoutId,
                "PAYOUT",
                oldStatus,
                status,
                request);

        return saved;
    }

    public ECreatorFinanceSettings getFinanceSettings(String userId) {
        return financeSettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ECreatorFinanceSettings settings = ECreatorFinanceSettings.builder()
                            .userId(userId)
                            .payoutMethods(List.of())
                            .taxVerificationStatus("UNVERIFIED")
                            .profileVerificationStatus("UNVERIFIED")
                            .taxForms(List.of())
                            .build();
                    return financeSettingsRepository.save(settings);
                });
    }

    public ECreatorFinanceSettings updateFinanceSettings(String userId, ECreatorFinanceSettings settings) {
        ECreatorFinanceSettings existing = getFinanceSettings(userId);
        settings.setId(existing.getId());
        settings.setUserId(userId);
        return financeSettingsRepository.save(settings);
    }
}
