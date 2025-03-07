package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.PreOrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreOrderRepository extends MongoRepository<PreOrderModel, String> {
}
