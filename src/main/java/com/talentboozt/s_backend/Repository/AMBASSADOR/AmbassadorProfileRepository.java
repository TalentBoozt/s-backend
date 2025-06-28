package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorProfileRepository extends MongoRepository<AmbassadorProfileModel, String> {
}
