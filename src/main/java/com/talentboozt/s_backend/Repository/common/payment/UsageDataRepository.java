package com.talentboozt.s_backend.Repository.common.payment;

import com.talentboozt.s_backend.Model.common.payment.UsageDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageDataRepository extends MongoRepository<UsageDataModel, String> {
    UsageDataModel findByCompanyId(String companyId);
}
