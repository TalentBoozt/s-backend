package com.talentboozt.s_backend.Repository.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.SchedulerLogModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulerLogRepository extends MongoRepository<SchedulerLogModel, String> {

    List<SchedulerLogModel> findByJobNameOrderByRunAtDesc(String jobName);
}
