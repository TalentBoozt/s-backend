package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb;

import com.talentboozt.s_backend.domains.audit_logs.model.StripeAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl.StripeAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StripeAuditLogRepository extends MongoRepository<StripeAuditLog, String>, StripeAuditLogCustomRepo {

    List<StripeAuditLog> findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(String status, int maxRetries);
}
