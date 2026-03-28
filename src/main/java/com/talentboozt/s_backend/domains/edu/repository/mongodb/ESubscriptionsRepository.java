package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;

import java.util.Optional;

@Repository
public interface ESubscriptionsRepository extends MongoRepository<ESubscriptions, String> {
    Optional<ESubscriptions> findByUserId(String userId);
    Optional<ESubscriptions> findByStripeCustomerId(String stripeCustomerId);
}
