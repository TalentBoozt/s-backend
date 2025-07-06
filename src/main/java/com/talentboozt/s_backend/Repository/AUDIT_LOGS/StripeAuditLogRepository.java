package com.talentboozt.s_backend.Repository.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.StripeAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StripeAuditLogRepository extends MongoRepository<StripeAuditLog, String> {

    List<StripeAuditLog> findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(String status, int maxRetries);
}
