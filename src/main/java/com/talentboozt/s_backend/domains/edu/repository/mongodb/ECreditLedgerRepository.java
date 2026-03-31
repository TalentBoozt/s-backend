package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.ECreditLedger;
import java.util.List;

@Repository
public interface ECreditLedgerRepository extends MongoRepository<ECreditLedger, String> {
    List<ECreditLedger> findByUserIdOrderByCreatedAtDesc(String userId);
}
