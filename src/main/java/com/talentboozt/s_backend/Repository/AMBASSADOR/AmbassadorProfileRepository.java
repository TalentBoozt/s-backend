package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AmbassadorProfileRepository extends MongoRepository<AmbassadorProfileModel, String> {
    Optional<AmbassadorProfileModel> findByEmployeeId(String userId);
}
