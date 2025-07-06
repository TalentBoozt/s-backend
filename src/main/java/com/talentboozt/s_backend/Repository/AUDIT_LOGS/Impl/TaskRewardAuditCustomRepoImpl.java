package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.TaskRewardAuditModel;
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
public class TaskRewardAuditCustomRepoImpl implements TaskRewardAuditCustomRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<TaskRewardAuditModel> search(String ambassadorId, String status, Pageable pageable) {
        Query query = new Query();

        if (ambassadorId != null && !ambassadorId.isBlank()) {
            query.addCriteria(Criteria.where("ambassadorId").regex(ambassadorId, "i"));
        }

        if (status != null && !status.isBlank()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        long count = mongoTemplate.count(query, TaskRewardAuditModel.class);
        query.with(pageable);

        List<TaskRewardAuditModel> logs = mongoTemplate.find(query, TaskRewardAuditModel.class);
        return new PageImpl<>(logs, pageable, count);
    }
}
