package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EPayouts;
import java.util.List;

@Repository
public interface EPayoutsRepository extends MongoRepository<EPayouts, String> {
    List<EPayouts> findByCreatorId(String creatorId);
}
