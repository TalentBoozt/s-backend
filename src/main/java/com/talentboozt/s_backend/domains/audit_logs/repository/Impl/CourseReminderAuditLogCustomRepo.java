package com.talentboozt.s_backend.domains.audit_logs.repository.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.CourseReminderAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseReminderAuditLogCustomRepo {
    Page<CourseReminderAuditLog> searchWithFilter(String filter, Pageable pageable);
}
