package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EAssignmentSubmissions;

import java.util.List;
import java.util.Optional;

@Repository
public interface EAssignmentSubmissionsRepository extends MongoRepository<EAssignmentSubmissions, String> {
    Optional<EAssignmentSubmissions> findByAssignmentIdAndUserId(String assignmentId, String userId);
    List<EAssignmentSubmissions> findByAssignmentIdInAndUserId(List<String> assignmentIds, String userId);
}
