package com.talentboozt.s_backend.domains.payment.repository.mongodb;

import com.talentboozt.s_backend.domains.payment.model.UsageDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageDataRepository extends MongoRepository<UsageDataModel, String> {
    UsageDataModel findByCompanyId(String companyId);
}
