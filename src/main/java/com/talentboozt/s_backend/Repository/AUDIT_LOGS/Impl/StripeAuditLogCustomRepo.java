package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.StripeAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StripeAuditLogCustomRepo {
    Page<StripeAuditLog> search(String eventType, String status, Pageable pageable);
}
