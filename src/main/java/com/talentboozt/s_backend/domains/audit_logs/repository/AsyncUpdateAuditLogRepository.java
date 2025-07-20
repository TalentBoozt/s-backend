package com.talentboozt.s_backend.domains.audit_logs.repository;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.Impl.AsyncUpdateAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsyncUpdateAuditLogRepository extends MongoRepository<AsyncUpdateAuditLog, String>, AsyncUpdateAuditLogCustomRepo {
    List<AsyncUpdateAuditLog> findByStatus(String status);

    AsyncUpdateAuditLog findTopByOrderByCreatedAtDesc();
}
