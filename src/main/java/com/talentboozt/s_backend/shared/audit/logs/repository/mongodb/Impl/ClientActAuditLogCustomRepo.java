package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.shared.audit.logs.model.ClientActAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientActAuditLogCustomRepo {
    Page<ClientActAuditLog> searchWithFilter(String filter, Pageable pageable);
}
