package com.talentboozt.s_backend.domains.portal.job_portal.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.job_portal.platform.model.PreOrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreOrderRepository extends MongoRepository<PreOrderModel, String> {
}
