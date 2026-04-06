package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tracks refund requests and Stripe refund events for course purchases.
 *
 * Supports both:
 * - Admin/system-initiated refunds (via API)
 * - Stripe-webhook-driven refunds (charge.refunded)
 *
 * Partial refunds are supported — a single transaction can have multiple
 * ERefund records as long as totalRefunded <= originalAmount.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_refunds")
public class ERefund {
    @Id
    private String id;

    /** Reference to the original transaction */
    @Indexed
    private String transactionId;

    /** The Stripe Checkout Session ID from the original purchase */
    @Indexed
    private String stripeCheckoutSessionId;

    /** Stripe Charge ID (from charge.refunded event) */
    @Indexed
    private String stripeChargeId;

    /** Stripe Refund ID (re_xxx) */
    @Indexed(unique = true, sparse = true)
    private String stripeRefundId;

    /** Buyer who purchased the course */
    @Indexed
    private String buyerId;

    /** Creator/seller who will lose earnings */
    @Indexed
    private String sellerId;

    /** The course being refunded */
    @Indexed
    private String courseId;

    /** Amount refunded (in the transaction's currency) */
    private Double refundAmount;

    /** Original transaction amount */
    private Double originalAmount;

    /** Currency */
    private String currency;

    /** Refund type */
    private RefundType type;

    /** Current status */
    @Indexed
    private RefundStatus status;

    /** Reason for the refund */
    private String reason;

    /** Whether enrollment was revoked as part of this refund */
    @Builder.Default
    private Boolean enrollmentRevoked = false;

    /** Whether the holding ledger entry was reversed */
    @Builder.Default
    private Boolean holdingReversed = false;

    /** Who initiated the refund */
    private String initiatedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum RefundType {
        FULL,
        PARTIAL
    }

    public enum RefundStatus {
        /** Refund request created locally */
        PENDING,
        /** Stripe has confirmed the refund */
        COMPLETED,
        /** Refund failed at Stripe */
        FAILED
    }
}
