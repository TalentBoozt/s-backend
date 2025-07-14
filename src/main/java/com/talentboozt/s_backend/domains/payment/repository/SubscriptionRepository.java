package com.talentboozt.s_backend.domains.payment.repository;

import com.talentboozt.s_backend.domains.payment.model.SubscriptionsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<SubscriptionsModel, String> {
    SubscriptionsModel findByCompanyId(String companyId);
    Optional<SubscriptionsModel> findBySubscriptionId(String subscriptionId);
}
