package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.EFraudFlag;
import java.util.List;

@Repository
public interface EFraudFlagRepository extends MongoRepository<EFraudFlag, String> {
    List<EFraudFlag> findByTargetUserId(String targetUserId);
    List<EFraudFlag> findByStatus(String status);
}
