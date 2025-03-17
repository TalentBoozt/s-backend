package com.talentboozt.s_backend.Repository.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL.PreOrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreOrderRepository extends MongoRepository<PreOrderModel, String> {
}
