package com.talentboozt.s_backend.domains.billing.repository.mongodb;

import com.talentboozt.s_backend.domains.billing.model.SubscriptionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BillingSubscriptionRepository extends MongoRepository<SubscriptionModel, String> {
    Optional<SubscriptionModel> findByOrganizationId(String organizationId);
}
