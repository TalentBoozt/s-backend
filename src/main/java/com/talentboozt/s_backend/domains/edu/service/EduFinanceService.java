package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;
import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EPayoutsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduFinanceService {

    private final ETransactionsRepository transactionsRepository;
    private final EPayoutsRepository payoutsRepository;

    public EduFinanceService(ETransactionsRepository transactionsRepository, EPayoutsRepository payoutsRepository) {
        this.transactionsRepository = transactionsRepository;
        this.payoutsRepository = payoutsRepository;
    }

    public RevenueSummaryDTO getRevenueSummary(String creatorId) {
        List<ETransactions> transactions = transactionsRepository.findBySellerId(creatorId);
        List<EPayouts> payouts = payoutsRepository.findByCreatorId(creatorId);

        double totalEarnings = transactions.stream()
                .mapToDouble(t -> t.getCreatorEarning() != null ? t.getCreatorEarning() : 0.0)
                .sum();

        double withdrawnAmount = payouts.stream()
                .filter(p -> p.getStatus() == EPayoutStatus.COMPLETED)
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
                
        double requestedAmount = payouts.stream()
                .filter(p -> p.getStatus() == EPayoutStatus.REQUESTED || p.getStatus() == EPayoutStatus.PROCESSING)
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        double pendingBalance = totalEarnings - (withdrawnAmount + requestedAmount);

        return RevenueSummaryDTO.builder()
                .creatorId(creatorId)
                .totalEarnings(totalEarnings)
                .withdrawnAmount(withdrawnAmount)
                .pendingBalance(pendingBalance)
                .totalTransactions(transactions.size())
                .build();
    }

    public EPayouts requestPayout(String creatorId, PayoutRequest request) {
        RevenueSummaryDTO summary = getRevenueSummary(creatorId);

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Payout amount must be greater than zero");
        }

        if (request.getAmount() > summary.getPendingBalance()) {
            throw new RuntimeException("Insufficient available balance for this payout request");
        }

        EPayouts payout = EPayouts.builder()
                .creatorId(creatorId)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .method(request.getMethod())
                .bankDetails(request.getBankDetails()) // Handled as string (ideally encrypted upstream)
                .paypalEmail(request.getPaypalEmail())
                .status(EPayoutStatus.REQUESTED)
                .requestedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        return payoutsRepository.save(payout);
    }
    
    public List<EPayouts> getPayoutHistory(String creatorId) {
        return payoutsRepository.findByCreatorId(creatorId);
    }

    // Admin Operation
    public EPayouts updatePayoutStatus(String payoutId, EPayoutStatus status) {
        EPayouts payout = payoutsRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout request not found"));
        
        payout.setStatus(status);
        if (status == EPayoutStatus.COMPLETED) {
            payout.setPaidAt(Instant.now());
            // Platform fee tracking can be added here if payout service deducts dynamic wire fees
        }
        
        return payoutsRepository.save(payout);
    }
}
