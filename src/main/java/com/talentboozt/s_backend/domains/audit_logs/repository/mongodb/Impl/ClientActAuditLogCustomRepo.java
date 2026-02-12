package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.ClientActAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientActAuditLogCustomRepo {
    Page<ClientActAuditLog> searchWithFilter(String filter, Pageable pageable);
}
