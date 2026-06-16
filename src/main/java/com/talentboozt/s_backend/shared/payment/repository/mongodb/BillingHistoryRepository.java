package com.talentboozt.s_backend.shared.payment.repository.mongodb;

import com.talentboozt.s_backend.shared.payment.model.BillingHistoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillingHistoryRepository extends MongoRepository<BillingHistoryModel, String> {
    List<BillingHistoryModel> findByCompanyId(String companyId);

    boolean existsBySessionId(String sessionId);
}

