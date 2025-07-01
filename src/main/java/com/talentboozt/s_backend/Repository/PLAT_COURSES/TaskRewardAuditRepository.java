package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.TaskRewardAuditModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRewardAuditRepository extends MongoRepository<TaskRewardAuditModel, String> {

    List<TaskRewardAuditModel> findByAmbassadorIdOrderByIssuedAtDesc(String ambassadorId);

    List<TaskRewardAuditModel> findByTaskIdOrderByIssuedAtDesc(String taskId);
}
