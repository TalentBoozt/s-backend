package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ETransactionsRepository extends MongoRepository<ETransactions, String> {
    List<ETransactions> findBySellerId(String sellerId);
    List<ETransactions> findByBuyerId(String buyerId);

    Optional<ETransactions> findByStripeCheckoutSessionId(String stripeCheckoutSessionId);

    List<ETransactions> findAllByStripeCheckoutSessionId(String stripeCheckoutSessionId);

    /** Finds PENDING transactions whose expiresAt has passed — candidates for cleanup. */
    List<ETransactions> findByPaymentStatusAndExpiresAtBefore(EPaymentStatus status, Instant cutoff);

    /** Fallback: finds old PENDING transactions created before cutoff (for records without expiresAt). */
    List<ETransactions> findByPaymentStatusAndCreatedAtBefore(EPaymentStatus status, Instant cutoff);
}
