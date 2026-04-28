package com.talentboozt.s_backend.domains.subscription.repository.mongodb;

import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    Optional<Subscription> findByUserId(String userId);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
