package com.talentboozt.s_backend.domains.ambassador.repository;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AmbassadorProfileRepository extends MongoRepository<AmbassadorProfileModel, String> {
    Optional<AmbassadorProfileModel> findByEmployeeId(String userId);
}
