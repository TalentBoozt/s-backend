package com.talentboozt.s_backend.domains.audit_logs.repository;

import com.talentboozt.s_backend.domains.audit_logs.model.TaskRewardAuditModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.Impl.TaskRewardAuditCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRewardAuditRepository extends MongoRepository<TaskRewardAuditModel, String>, TaskRewardAuditCustomRepo {

    List<TaskRewardAuditModel> findByAmbassadorIdOrderByIssuedAtDesc(String ambassadorId);

    List<TaskRewardAuditModel> findByTaskIdOrderByIssuedAtDesc(String taskId);
}
