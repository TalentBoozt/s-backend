package com.talentboozt.s_backend.domains.audit_logs.repository.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SchedulerLogCustomRepoImpl implements SchedulerLogCustomRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<SchedulerLogModel> search(String jobName, String status, Pageable pageable) {
        Query query = new Query();

        if (jobName != null && !jobName.isBlank()) {
            query.addCriteria(Criteria.where("jobName").regex(jobName, "i"));
        }

        if (status != null && !status.isBlank()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        long count = mongoTemplate.count(query, SchedulerLogModel.class);
        query.with(pageable);

        List<SchedulerLogModel> logs = mongoTemplate.find(query, SchedulerLogModel.class);
        return new PageImpl<>(logs, pageable, count);
    }
}
