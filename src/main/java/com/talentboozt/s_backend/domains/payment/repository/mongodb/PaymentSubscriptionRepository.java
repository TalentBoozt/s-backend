package com.talentboozt.s_backend.domains.payment.repository.mongodb;

import com.talentboozt.s_backend.domains.payment.model.PaymentSubscriptionsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentSubscriptionRepository extends MongoRepository<PaymentSubscriptionsModel, String> {
    PaymentSubscriptionsModel findByCompanyId(String companyId);
    Optional<PaymentSubscriptionsModel> findBySubscriptionId(String subscriptionId);
}
