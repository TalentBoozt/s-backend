package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.TrainerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerProfileRepository extends MongoRepository<TrainerProfile, String> {
    TrainerProfile findByEmployeeId(String employeeId);
}
