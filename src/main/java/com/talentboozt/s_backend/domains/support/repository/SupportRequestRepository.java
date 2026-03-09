package com.talentboozt.s_backend.domains.support.repository;

import com.talentboozt.s_backend.domains.support.model.SupportRequestModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRequestRepository extends MongoRepository<SupportRequestModel, String> {
}
