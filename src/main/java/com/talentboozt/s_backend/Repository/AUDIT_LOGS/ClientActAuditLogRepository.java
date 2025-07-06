package com.talentboozt.s_backend.Repository.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.ClientActAuditLog;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl.ClientActAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientActAuditLogRepository extends MongoRepository<ClientActAuditLog, String>, ClientActAuditLogCustomRepo {
}
