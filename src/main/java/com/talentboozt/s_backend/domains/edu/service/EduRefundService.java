package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.model.ERefund;
import com.talentboozt.s_backend.domains.edu.model.ERefund.RefundStatus;
import com.talentboozt.s_backend.domains.edu.model.ERefund.RefundType;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ERefundRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Handles refund processing for course purchases.
 *
 * Two entry points:
 * 1. Stripe webhook (charge.refunded) → processStripeRefund()
 * 2. Admin API (manual refund request) → initiateRefund()
 *
 * On refund:
 * - Transaction status updated to REFUNDED
 * - Holding ledger entry reversed (HELD→REFUNDED or CLEARED→clawback note)
 * - Enrollment revoked for full refunds
 * - Creator earnings adjusted
 */
@Service
public class EduRefundService {

    private static final Logger log = LoggerFactory.getLogger(EduRefundService.class);

    private final ERefundRepository refundRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EHoldingLedgerRepository holdingLedgerRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EduAuditService auditService;
    private final EduLedgerService ledgerService;

    public EduRefundService(ERefundRepository refundRepository,
                            ETransactionsRepository transactionsRepository,
                            EHoldingLedgerRepository holdingLedgerRepository,
                            EEnrollmentsRepository enrollmentsRepository,
                            EduAuditService auditService,
                            EduLedgerService ledgerService) {
        this.refundRepository = refundRepository;
        this.transactionsRepository = transactionsRepository;
        this.holdingLedgerRepository = holdingLedgerRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.auditService = auditService;
        this.ledgerService = ledgerService;
    }

    /**
     * Processes a refund event from Stripe's charge.refunded webhook.
     *
     * @param stripeChargeId Stripe charge ID (ch_xxx)
     * @param stripeRefundId Stripe refund ID (re_xxx)
     * @param amountRefunded Amount refunded in cents
     * @param amountTotalCents Total charge amount in cents (for full/partial detection)
     * @param sessionId Stripe checkout session ID (from charge metadata or payment intent)
     */
    @Transactional
    public void processStripeRefund(String stripeChargeId, String stripeRefundId,
                                     long amountRefundedCents, long amountTotalCents,
                                     String sessionId) {
        // Idempotency check — skip if this Stripe refund was already processed
        if (refundRepository.existsByStripeRefundId(stripeRefundId)) {
            log.info("Skipping duplicate Stripe refund: {}", stripeRefundId);
            return;
        }

        double refundAmount = amountRefundedCents / 100.0;
        double totalAmount = amountTotalCents / 100.0;
        boolean isFullRefund = amountRefundedCents >= amountTotalCents;

        // Find the original transaction(s) by session ID
        // For single-course: findByStripeCheckoutSessionId
        // For multi-course: findAllByStripeCheckoutSessionId
        List<ETransactions> transactions = transactionsRepository
                .findAllByStripeCheckoutSessionId(sessionId);

        if (transactions.isEmpty()) {
            // Try single-course lookup
            transactionsRepository.findByStripeCheckoutSessionId(sessionId)
                    .ifPresent(transactions::add);
        }

        if (transactions.isEmpty()) {
            log.warn("No transactions found for Stripe refund {} (session={}). Creating orphan refund record.",
                    stripeRefundId, sessionId);
            // Still create a refund record for audit trail
            ERefund orphanRefund = ERefund.builder()
                    .stripeChargeId(stripeChargeId)
                    .stripeRefundId(stripeRefundId)
                    .stripeCheckoutSessionId(sessionId)
                    .refundAmount(refundAmount)
                    .originalAmount(totalAmount)
                    .type(isFullRefund ? RefundType.FULL : RefundType.PARTIAL)
                    .status(RefundStatus.COMPLETED)
                    .reason("Stripe webhook — no matching transaction found")
                    .initiatedBy("STRIPE")
                    .build();
            refundRepository.save(orphanRefund);
            return;
        }

        if (isFullRefund) {
            // Full refund — process all transactions in the session
            for (ETransactions tx : transactions) {
                processTransactionRefund(tx, tx.getAmount(), stripeChargeId,
                        stripeRefundId, RefundType.FULL, "Stripe charge.refunded (full)");
            }
        } else {
            // Partial refund — proportionally distribute across transactions
            double remainingRefund = refundAmount;
            for (ETransactions tx : transactions) {
                if (remainingRefund <= 0) break;
                double txAmount = tx.getAmount() != null ? tx.getAmount() : 0.0;
                double txRefund = Math.min(remainingRefund, txAmount);
                processTransactionRefund(tx, txRefund, stripeChargeId,
                        stripeRefundId, RefundType.PARTIAL, "Stripe charge.refunded (partial)");
                remainingRefund -= txRefund;
            }
        }

        log.info("Processed Stripe refund {} for session {} (amount={}, full={})",
                stripeRefundId, sessionId, refundAmount, isFullRefund);
    }

    /**
     * Admin-initiated refund. Creates a refund record and updates local state.
     * NOTE: This does NOT trigger Stripe refund — that must be done separately
     * via Stripe Dashboard or a future Stripe Refund API integration.
     */
    @Transactional
    public ERefund initiateRefund(String transactionId, Double amount, String reason, String adminId) {
        ETransactions tx = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new EduResourceNotFoundException("Transaction not found: " + transactionId));

        if (tx.getPaymentStatus() != EPaymentStatus.SUCCESS) {
            throw new EduBadRequestException("Can only refund SUCCESS transactions. Current status: " + tx.getPaymentStatus());
        }

        // Validate refund amount
        double alreadyRefunded = getRefundedAmount(transactionId);
        double maxRefundable = (tx.getAmount() != null ? tx.getAmount() : 0.0) - alreadyRefunded;

        if (amount == null || amount <= 0) {
            throw new EduBadRequestException("Refund amount must be positive");
        }
        if (amount > maxRefundable) {
            throw new EduBadRequestException(
                    String.format("Refund amount $%.2f exceeds maximum refundable $%.2f (already refunded: $%.2f)",
                            amount, maxRefundable, alreadyRefunded));
        }

        boolean isFullRefund = Math.abs(amount - maxRefundable) < 0.01;
        RefundType type = isFullRefund ? RefundType.FULL : RefundType.PARTIAL;

        return processTransactionRefund(tx, amount, null, null, type, reason != null ? reason : "Admin refund");
    }

    /**
     * Core refund processing logic used by both webhook and admin flows.
     */
    private ERefund processTransactionRefund(ETransactions tx, double refundAmount,
                                              String stripeChargeId, String stripeRefundId,
                                              RefundType type, String reason) {
        // 1. Create refund record
        ERefund refund = ERefund.builder()
                .transactionId(tx.getId())
                .stripeCheckoutSessionId(tx.getStripeCheckoutSessionId())
                .stripeChargeId(stripeChargeId)
                .stripeRefundId(stripeRefundId)
                .buyerId(tx.getBuyerId())
                .sellerId(tx.getSellerId())
                .courseId(tx.getCourseId())
                .refundAmount(refundAmount)
                .originalAmount(tx.getAmount())
                .currency(tx.getCurrency())
                .type(type)
                .status(RefundStatus.COMPLETED)
                .reason(reason)
                .initiatedBy(stripeRefundId != null ? "STRIPE" : "ADMIN")
                .build();

        // 2. Update transaction status
        if (type == RefundType.FULL) {
            tx.setPaymentStatus(EPaymentStatus.REFUNDED);
        } else {
            // For partial refunds, keep SUCCESS but track refund amount
            // Transaction is only marked REFUNDED when fully refunded
            double totalRefunded = getRefundedAmount(tx.getId()) + refundAmount;
            if (totalRefunded >= (tx.getAmount() != null ? tx.getAmount() : 0.0)) {
                tx.setPaymentStatus(EPaymentStatus.REFUNDED);
            }
        }
        tx.setUpdatedAt(Instant.now());
        transactionsRepository.save(tx);

        // 3. Reverse holding ledger entry
        reverseHoldingLedger(tx, refundAmount, type);
        refund.setHoldingReversed(true);

        // 4. Revoke enrollment for full refunds
        if (type == RefundType.FULL && tx.getBuyerId() != null && tx.getCourseId() != null) {
            revokeEnrollment(tx.getBuyerId(), tx.getCourseId());
            refund.setEnrollmentRevoked(true);
        }

        ERefund saved = refundRepository.save(refund);

        // Record double entry ledger
        double txAmount = tx.getAmount() != null ? tx.getAmount() : 0.0;
        ledgerService.recordRefund(saved.getId(), tx.getId(), tx.getBuyerId(),
                tx.getSellerId(), refundAmount, txAmount,
                tx.getCurrency(), tx.getCourseId(), type == RefundType.FULL);

        // 5. Audit log
        auditService.logAction(
                refund.getInitiatedBy(),
                "REFUND_" + type.name(),
                tx.getId(),
                "TRANSACTION",
                tx.getPaymentStatus(),
                EPaymentStatus.REFUNDED
        );

        log.info("Refund processed: tx={}, amount={}, type={}, course={}, buyer={}",
                tx.getId(), refundAmount, type, tx.getCourseId(), tx.getBuyerId());

        return saved;
    }

    /**
     * Reverses the holding ledger entry associated with a transaction.
     * If funds are still HELD → mark as REFUNDED
     * If funds are already CLEARED → create a negative "clawback" entry
     */
    private void reverseHoldingLedger(ETransactions tx, double refundAmount, RefundType type) {
        holdingLedgerRepository.findByTransactionId(tx.getTransactionId()).ifPresent(ledger -> {
            if (ledger.getStatus() == EHoldingStatus.HELD) {
                // Funds still in holding — simple reversal
                if (type == RefundType.FULL) {
                    ledger.setStatus(EHoldingStatus.REFUNDED);
                } else {
                    // Partial refund while funds still held — reduce amount
                    double newAmount = Math.max(0, (ledger.getAmount() != null ? ledger.getAmount() : 0.0) - refundAmount);
                    ledger.setAmount(newAmount);
                    if (newAmount <= 0) {
                        ledger.setStatus(EHoldingStatus.REFUNDED);
                    }
                }
                ledger.setUpdatedAt(Instant.now());
                holdingLedgerRepository.save(ledger);
                log.info("Reversed holding ledger for tx={}: status={}", tx.getTransactionId(), ledger.getStatus());
            } else if (ledger.getStatus() == EHoldingStatus.CLEARED) {
                // Funds already cleared to creator — log clawback need
                log.warn("CLAWBACK NEEDED: Transaction {} was already cleared. Refund amount={}, creator={}",
                        tx.getTransactionId(), refundAmount, tx.getSellerId());
                
                // Create clawback holding record to track post-clearance deficit
                EHoldingLedger clawback = EHoldingLedger.builder()
                        .beneficiaryId(tx.getSellerId())
                        .beneficiaryType(com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType.CREATOR)
                        .transactionId(tx.getId())
                        .courseId(tx.getCourseId())
                        .amount(-refundAmount) // Record as a negative amount for visibility
                        .currency(tx.getCurrency())
                        .status(EHoldingStatus.CLAWBACK)
                        .createdAt(Instant.now())
                        .build();
                holdingLedgerRepository.save(clawback);
            }
        });
    }

    /**
     * Revokes enrollment for a full refund. The user loses access to the course.
     */
    private void revokeEnrollment(String userId, String courseId) {
        enrollmentsRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(enrollment -> {
            enrollmentsRepository.delete(enrollment);
            log.info("Revoked enrollment: user={}, course={}", userId, courseId);
        });
    }

    /**
     * Returns the total amount already refunded for a transaction.
     */
    public double getRefundedAmount(String transactionId) {
        return refundRepository.findByTransactionIdAndStatus(transactionId, RefundStatus.COMPLETED)
                .stream()
                .mapToDouble(r -> r.getRefundAmount() != null ? r.getRefundAmount() : 0.0)
                .sum();
    }

    /**
     * Returns refund history for a buyer.
     */
    public List<ERefund> getRefundsByBuyer(String buyerId) {
        return refundRepository.findByBuyerId(buyerId);
    }

    /**
     * Returns refunds affecting a creator/seller.
     */
    public List<ERefund> getRefundsBySeller(String sellerId) {
        return refundRepository.findBySellerId(sellerId);
    }
}
