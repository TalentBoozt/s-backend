package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.shared.audit.logs.model.CourseReminderAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseReminderAuditLogCustomRepo {
    Page<CourseReminderAuditLog> searchWithFilter(String filter, Pageable pageable);
}
