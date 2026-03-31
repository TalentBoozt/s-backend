package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.ETrustScores;

import java.util.Optional;

@Repository
public interface ETrustScoresRepository extends MongoRepository<ETrustScores, String> {
    Optional<ETrustScores> findByCreatorId(String creatorId);
}
