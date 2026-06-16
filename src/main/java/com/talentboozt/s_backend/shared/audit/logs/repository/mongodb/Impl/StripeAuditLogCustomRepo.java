package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.shared.audit.logs.model.StripeAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StripeAuditLogCustomRepo {
    Page<StripeAuditLog> search(String eventType, String status, Pageable pageable);
}
