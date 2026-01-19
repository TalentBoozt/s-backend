package com.talentboozt.s_backend.domains.audit_logs.repository.Impl;

import com.mongodb.client.model.Filters;
import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class AsyncUpdateAuditLogCustomRepoImpl implements AsyncUpdateAuditLogCustomRepo {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<AsyncUpdateAuditLog> searchWithFilter(String filter, Pageable pageable) {
        Bson criteria = new Document();
        if (filter != null && !filter.isBlank()) {
            String[] parts = filter.split(":");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                criteria = Filters.regex(key, ".*" + value + ".*", "i");
            }
        }

        List<AsyncUpdateAuditLog> results = mongoTemplate.getCollection("async_update_audit_log")
                .find(criteria)
                .sort(new Document("createdAt", -1))
                .skip((int) pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(doc -> mongoTemplate.getConverter().read(AsyncUpdateAuditLog.class, Objects.requireNonNull(doc)))
                .into(new ArrayList<>());

        long total = mongoTemplate.getCollection("async_update_audit_log").countDocuments(criteria);

        return new PageImpl<>(Objects.requireNonNull(results), pageable, total);
    }
}
