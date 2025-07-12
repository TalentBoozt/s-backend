package com.talentboozt.s_backend.domains.payment.repository;

import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillingHistoryRepository extends MongoRepository<BillingHistoryModel, String> {
    List<BillingHistoryModel> findByCompanyId(String companyId);
}

