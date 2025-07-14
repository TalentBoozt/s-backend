package com.talentboozt.s_backend.domains.audit_logs.repository.Impl;

import com.mongodb.client.model.Filters;
import com.talentboozt.s_backend.domains.audit_logs.model.ClientActAuditLog;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientActAuditLogCustomRepoImpl implements ClientActAuditLogCustomRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<ClientActAuditLog> searchWithFilter(String filter, Pageable pageable) {
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
        List<ClientActAuditLog> results = mongoTemplate.getCollection("client_act_audit_log")
                .find(criteria)
                .sort(new Document("timestamp", -1))
                .skip((int) pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(doc -> mongoTemplate.getConverter().read(ClientActAuditLog.class, doc))
                .into(new ArrayList<>());

        long total = mongoTemplate.getCollection("client_act_audit_log").countDocuments(criteria);

        return new PageImpl<>(results, pageable, total);
    }
}
