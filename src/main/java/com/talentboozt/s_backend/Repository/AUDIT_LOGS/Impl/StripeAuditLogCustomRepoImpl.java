package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.StripeAuditLog;
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
public class StripeAuditLogCustomRepoImpl implements StripeAuditLogCustomRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<StripeAuditLog> search(String eventType, String status, Pageable pageable) {
        Query query = new Query();

        if (eventType != null && !eventType.isBlank()) {
            query.addCriteria(Criteria.where("eventType").regex(eventType, "i"));
        }

        if (status != null && !status.isBlank()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        long count = mongoTemplate.count(query, StripeAuditLog.class);
        query.with(pageable);

        List<StripeAuditLog> logs = mongoTemplate.find(query, StripeAuditLog.class);
        return new PageImpl<>(logs, pageable, count);
    }
}
