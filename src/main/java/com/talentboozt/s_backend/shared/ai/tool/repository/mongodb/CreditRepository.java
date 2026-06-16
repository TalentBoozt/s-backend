package com.talentboozt.s_backend.shared.ai.tool.repository.mongodb;

import com.talentboozt.s_backend.shared.ai.tool.model.CreditRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditRepository extends MongoRepository<CreditRecord, String> {
    CreditRecord findById(String key, Class<CreditRecord> creditRecordClass);
}
