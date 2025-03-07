package com.talentboozt.s_backend.Repository.payment;

import com.talentboozt.s_backend.Model.payment.UsageDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageDataRepository extends MongoRepository<UsageDataModel, String> {
    UsageDataModel findByCompanyId(String companyId);
}
