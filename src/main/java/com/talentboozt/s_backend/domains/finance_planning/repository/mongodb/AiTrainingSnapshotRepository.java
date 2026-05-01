package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.AiTrainingSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AiTrainingSnapshotRepository extends MongoRepository<AiTrainingSnapshot, String> {
    List<AiTrainingSnapshot> findByOrganizationIdAndProjectId(String organizationId, String projectId);
}