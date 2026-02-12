package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl.SchedulerLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulerLogRepository extends MongoRepository<SchedulerLogModel, String>, SchedulerLogCustomRepo {

    List<SchedulerLogModel> findByJobNameOrderByRunAtDesc(String jobName);
}
