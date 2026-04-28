package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EWallet;
import com.talentboozt.s_backend.domains.edu.model.EWalletTransaction;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWalletRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWalletTransactionRepository;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduWalletService {

    private final EWalletRepository walletRepository;
    private final EWalletTransactionRepository transactionRepository;

    public EWallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    EWallet wallet = EWallet.builder()
                            .userId(userId)
                            .availableBalance(0.0)
                            .pendingBalance(0.0)
                            .currency("USD")
                            .updatedAt(Instant.now())
                            .build();
                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public void addSale(String userId, Double amount, String referenceId) {
        log.info("Adding sale to pending balance for user: {}, amount: {}", userId, amount);
        EWallet wallet = getOrCreateWallet(userId);
        wallet.setPendingBalance(round2(wallet.getPendingBalance() + amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        createTransaction(userId, EWalletTransaction.TransactionType.SALE, amount, 
                EWalletTransaction.TransactionStatus.PENDING, referenceId, "Course sale - pending clearance");
    }

    @Transactional
    public void releaseFunds(String userId, Double amount, String referenceId) {
        log.info("Releasing funds for user: {}, amount: {}", userId, amount);
        EWallet wallet = getOrCreateWallet(userId);
        
        // Safety check
        if (wallet.getPendingBalance() < amount) {
            log.warn("Attempting to release more funds than pending for user {}. Adjusting to available pending.", userId);
            amount = wallet.getPendingBalance();
        }

        wallet.setPendingBalance(round2(wallet.getPendingBalance() - amount));
        wallet.setAvailableBalance(round2(wallet.getAvailableBalance() + amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        createTransaction(userId, EWalletTransaction.TransactionType.FUND_RELEASE, amount, 
                EWalletTransaction.TransactionStatus.COMPLETED, referenceId, "Funds released to available balance");
    }

    @Transactional
    public void processRefund(String userId, Double amount, String referenceId) {
        log.info("Processing refund for user: {}, amount: {}", userId, amount);
        EWallet wallet = getOrCreateWallet(userId);

        // Deduct from pending first, then available if needed
        if (wallet.getPendingBalance() >= amount) {
            wallet.setPendingBalance(round2(wallet.getPendingBalance() - amount));
        } else {
            double remaining = amount - wallet.getPendingBalance();
            wallet.setPendingBalance(0.0);
            wallet.setAvailableBalance(round2(wallet.getAvailableBalance() - remaining));
        }

        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        createTransaction(userId, EWalletTransaction.TransactionType.REFUND, amount, 
                EWalletTransaction.TransactionStatus.COMPLETED, referenceId, "Refund processed");
    }

    @Transactional
    public void requestPayout(String userId, Double amount, String referenceId) {
        log.info("Processing payout request for user: {}, amount: {}", userId, amount);
        EWallet wallet = getOrCreateWallet(userId);

        if (wallet.getAvailableBalance() < amount) {
            throw new EduBadRequestException("Insufficient available balance for payout.");
        }

        wallet.setAvailableBalance(round2(wallet.getAvailableBalance() - amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        createTransaction(userId, EWalletTransaction.TransactionType.PAYOUT, amount, 
                EWalletTransaction.TransactionStatus.COMPLETED, referenceId, "Payout completed");
    }

    private void createTransaction(String userId, EWalletTransaction.TransactionType type, Double amount, 
                                 EWalletTransaction.TransactionStatus status, String referenceId, String description) {
        EWalletTransaction tx = EWalletTransaction.builder()
                .userId(userId)
                .type(type)
                .amount(amount)
                .currency("USD")
                .status(status)
                .referenceId(referenceId)
                .description(description)
                .createdAt(Instant.now())
                .build();
        transactionRepository.save(tx);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
