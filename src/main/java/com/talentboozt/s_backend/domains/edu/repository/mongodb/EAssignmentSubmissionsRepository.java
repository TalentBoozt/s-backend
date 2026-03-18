package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EAssignmentSubmissions;

@Repository
public interface EAssignmentSubmissionsRepository extends MongoRepository<EAssignmentSubmissions, String> {
}
