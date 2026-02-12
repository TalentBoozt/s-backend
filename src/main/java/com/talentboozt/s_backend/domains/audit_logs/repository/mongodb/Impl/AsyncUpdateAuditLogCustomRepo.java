package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AsyncUpdateAuditLogCustomRepo {
    Page<AsyncUpdateAuditLog> searchWithFilter(String filter, Pageable pageable);
}
