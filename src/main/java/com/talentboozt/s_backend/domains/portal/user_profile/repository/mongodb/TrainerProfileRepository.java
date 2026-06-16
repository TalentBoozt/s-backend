package com.talentboozt.s_backend.domains.portal.user_profile.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.user_profile.model.TrainerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends MongoRepository<TrainerProfile, String> {
    Optional<TrainerProfile> findByEmployeeId(String employeeId);
}
