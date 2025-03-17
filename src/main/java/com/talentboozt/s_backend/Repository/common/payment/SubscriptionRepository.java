package com.talentboozt.s_backend.Repository.common.payment;

import com.talentboozt.s_backend.Model.common.payment.SubscriptionsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<SubscriptionsModel, String> {
    SubscriptionsModel findByCompanyId(String companyId);
}
