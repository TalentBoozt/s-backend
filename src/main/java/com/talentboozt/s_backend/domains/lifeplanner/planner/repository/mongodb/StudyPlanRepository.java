package com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends MongoRepository<StudyPlan, String> {
    Optional<StudyPlan> findByGoalId(String goalId);
    List<StudyPlan> findByUserId(String userId);
    long countByStatus(String status);
    List<StudyPlan> findByStatus(String status);
}
