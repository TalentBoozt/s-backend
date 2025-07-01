package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorPointAudit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AmbassadorPointAuditRepository extends MongoRepository<AmbassadorPointAudit, String> {
    List<AmbassadorPointAudit> findByAmbassadorId(String ambassadorId);
}
