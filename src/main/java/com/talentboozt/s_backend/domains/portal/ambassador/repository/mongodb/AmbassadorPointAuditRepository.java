package com.talentboozt.s_backend.domains.portal.ambassador.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.ambassador.model.AmbassadorPointAudit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AmbassadorPointAuditRepository extends MongoRepository<AmbassadorPointAudit, String> {
    List<AmbassadorPointAudit> findByAmbassadorId(String ambassadorId);
}
