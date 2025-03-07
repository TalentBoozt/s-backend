package com.talentboozt.s_backend.Repository.payment;

import com.talentboozt.s_backend.Model.payment.BillingHistoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillingHistoryRepository extends MongoRepository<BillingHistoryModel, String> {
    List<BillingHistoryModel> findByCompanyId(String companyId);
}

