package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.ClientActAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientActAuditLogCustomRepo {
    Page<ClientActAuditLog> searchWithFilter(String filter, Pageable pageable);
}
