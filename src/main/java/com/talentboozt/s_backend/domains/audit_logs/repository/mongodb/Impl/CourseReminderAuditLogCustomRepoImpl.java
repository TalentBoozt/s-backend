package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl;

import com.mongodb.client.model.Filters;
import com.talentboozt.s_backend.domains.audit_logs.model.CourseReminderAuditLog;
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
public class CourseReminderAuditLogCustomRepoImpl implements CourseReminderAuditLogCustomRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<CourseReminderAuditLog> searchWithFilter(String filter, Pageable pageable) {
        Bson criteria = new Document();

        if (filter != null && !filter.isBlank()) {
            // Example: filter = "userId:john" or "action:RATE_LIMIT_EXCEEDED"
            String[] parts = filter.split(":");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                criteria = Filters.regex(key, ".*" + value + ".*", "i");
            }
        }

        // Run query with limit, skip, and sort
        List<CourseReminderAuditLog> results = mongoTemplate.getCollection("course_reminder_audit_logs")
                .find(criteria)
                .sort(new Document("timestamp", -1))
                .skip((int) pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(doc -> mongoTemplate.getConverter().read(CourseReminderAuditLog.class, Objects.requireNonNull(doc)))
                .into(new ArrayList<>());

        long total = mongoTemplate.getCollection("course_reminder_audit_logs").countDocuments(criteria);

        return new PageImpl<>(Objects.requireNonNull(results), pageable, total);
    }
}
