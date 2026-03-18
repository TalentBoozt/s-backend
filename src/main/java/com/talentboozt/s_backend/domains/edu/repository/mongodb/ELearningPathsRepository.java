package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ELearningPaths;
import java.util.List;

@Repository
public interface ELearningPathsRepository extends MongoRepository<ELearningPaths, String> {
    List<ELearningPaths> findByWorkspaceId(String workspaceId);
}
