package com.talentboozt.s_backend.domains.user.repository.mongodb;

import com.talentboozt.s_backend.domains.user.model.TrainerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends MongoRepository<TrainerProfile, String> {
    Optional<TrainerProfile> findByEmployeeId(String employeeId);
}
