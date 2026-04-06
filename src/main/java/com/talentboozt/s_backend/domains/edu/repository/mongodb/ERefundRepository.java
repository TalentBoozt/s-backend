package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.ERefund;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ERefundRepository extends MongoRepository<ERefund, String> {

    /** Find all refunds for a specific transaction */
    List<ERefund> findByTransactionId(String transactionId);

    /** Find refund by Stripe refund ID (for webhook dedup) */
    Optional<ERefund> findByStripeRefundId(String stripeRefundId);

    /** Check if a Stripe refund has already been processed */
    boolean existsByStripeRefundId(String stripeRefundId);

    /** Find all refunds for a buyer */
    List<ERefund> findByBuyerId(String buyerId);

    /** Find all refunds affecting a seller/creator */
    List<ERefund> findBySellerId(String sellerId);

    /** Find all refunds for a specific course */
    List<ERefund> findByCourseId(String courseId);

    /** Sum of refund amounts for a transaction (for partial refund validation) */
    List<ERefund> findByTransactionIdAndStatus(String transactionId, ERefund.RefundStatus status);
}
