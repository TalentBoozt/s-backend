package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EAffiliateCommissions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EAffiliateCommissionsRepository extends MongoRepository<EAffiliateCommissions, String> {
    List<EAffiliateCommissions> findByAffiliateId(String affiliateId);
    List<EAffiliateCommissions> findByTransactionId(String transactionId);
}
