package com.talentboozt.s_backend.domains.portal.insights.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.insights.model.InsightModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InsightRepository extends MongoRepository<InsightModel, String> {
    List<InsightModel> findByOrganizationIdOrderByCreatedAtDesc(String organizationId);
}
