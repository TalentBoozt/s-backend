package com.talentboozt.s_backend.domains.recruiter.repository.mongodb;

import com.talentboozt.s_backend.domains.recruiter.model.RecruiterModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface RecruiterRepository extends MongoRepository<RecruiterModel, String> {
    Optional<RecruiterModel> findByUserId(String userId);
    List<RecruiterModel> findByOrganizationId(String organizationId);
}
