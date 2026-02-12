package com.talentboozt.s_backend.domains.plat_job_portal.repository.mongodb;

import com.talentboozt.s_backend.domains.plat_job_portal.model.PreOrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreOrderRepository extends MongoRepository<PreOrderModel, String> {
}
