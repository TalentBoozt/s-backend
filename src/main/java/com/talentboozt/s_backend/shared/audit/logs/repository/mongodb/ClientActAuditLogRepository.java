package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb;

import com.talentboozt.s_backend.shared.audit.logs.model.ClientActAuditLog;
import com.talentboozt.s_backend.shared.audit.logs.repository.mongodb.Impl.ClientActAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientActAuditLogRepository extends MongoRepository<ClientActAuditLog, String>, ClientActAuditLogCustomRepo {
}
