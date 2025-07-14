package com.talentboozt.s_backend.domains.audit_logs.repository;

import com.talentboozt.s_backend.domains.audit_logs.model.ClientActAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.Impl.ClientActAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientActAuditLogRepository extends MongoRepository<ClientActAuditLog, String>, ClientActAuditLogCustomRepo {
}
