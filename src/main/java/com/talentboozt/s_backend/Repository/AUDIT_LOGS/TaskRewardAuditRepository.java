package com.talentboozt.s_backend.Repository.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.TaskRewardAuditModel;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl.TaskRewardAuditCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRewardAuditRepository extends MongoRepository<TaskRewardAuditModel, String>, TaskRewardAuditCustomRepo {

    List<TaskRewardAuditModel> findByAmbassadorIdOrderByIssuedAtDesc(String ambassadorId);

    List<TaskRewardAuditModel> findByTaskIdOrderByIssuedAtDesc(String taskId);
}
