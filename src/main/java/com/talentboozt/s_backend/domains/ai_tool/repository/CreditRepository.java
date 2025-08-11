package com.talentboozt.s_backend.domains.ai_tool.repository;

import com.talentboozt.s_backend.domains.ai_tool.model.CreditRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditRepository extends MongoRepository<CreditRecord, String> {
    CreditRecord findById(String key, Class<CreditRecord> creditRecordClass);
}
