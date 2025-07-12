package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.CourseReminderAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReminderAuditLogRepository extends MongoRepository<CourseReminderAuditLog, String> {
}

