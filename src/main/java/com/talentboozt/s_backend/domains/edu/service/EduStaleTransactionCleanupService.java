package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scheduled job that expires stale PENDING transactions.
 * 
 * Stripe checkout sessions expire after 24 hours by default. If the webhook 
 * for checkout.session.expired is missed (network issues, Stripe downtime),
 * this cron ensures we still clean up orphaned PENDING records.
 * 
 * Two strategies:
 * 1. Primary: Uses the `expiresAt` field (set on new transactions)
 * 2. Fallback: Uses `createdAt` for legacy records without `expiresAt`
 * 
 * Runs every 6 hours to minimize stale records without excessive DB pressure.
 */
@Service
public class EduStaleTransactionCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(EduStaleTransactionCleanupService.class);

    /** Grace period beyond Stripe's session expiration before we mark as EXPIRED */
    private static final long GRACE_HOURS = 2;

    /** Fallback cutoff for legacy records without expiresAt field */
    private static final long FALLBACK_STALE_HOURS = 26; // 24h session + 2h grace

    private final ETransactionsRepository transactionsRepository;

    public EduStaleTransactionCleanupService(ETransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    /**
     * Runs every 6 hours. Finds PENDING transactions past their expiration and marks them EXPIRED.
     */
    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours at the top of the hour
    public void expireStaleTransactions() {
        logger.info("Starting stale transaction cleanup job...");

        Instant now = Instant.now();
        Set<String> processedIds = new HashSet<>();
        int count = 0;

        // Strategy 1: Records with explicit expiresAt (includes grace period)
        Instant expiryWithGrace = now.minus(GRACE_HOURS, ChronoUnit.HOURS);
        // We want records where expiresAt is BEFORE (now - grace), meaning they expired
        // at least GRACE_HOURS ago
        List<ETransactions> expiredByField = transactionsRepository
                .findByPaymentStatusAndExpiresAtBefore(EPaymentStatus.PENDING, now);

        for (ETransactions tx : expiredByField) {
            tx.setPaymentStatus(EPaymentStatus.EXPIRED);
            tx.setUpdatedAt(now);
            transactionsRepository.save(tx);
            processedIds.add(tx.getId());
            count++;
        }

        // Strategy 2: Fallback for legacy records without expiresAt
        Instant fallbackCutoff = now.minus(FALLBACK_STALE_HOURS, ChronoUnit.HOURS);
        List<ETransactions> staleByAge = transactionsRepository
                .findByPaymentStatusAndCreatedAtBefore(EPaymentStatus.PENDING, fallbackCutoff);

        for (ETransactions tx : staleByAge) {
            if (!processedIds.contains(tx.getId())) {
                tx.setPaymentStatus(EPaymentStatus.EXPIRED);
                tx.setUpdatedAt(now);
                transactionsRepository.save(tx);
                count++;
            }
        }

        if (count > 0) {
            logger.info("Stale transaction cleanup completed. Expired {} PENDING transactions.", count);
        } else {
            logger.info("Stale transaction cleanup completed. No stale transactions found.");
        }
    }
}
