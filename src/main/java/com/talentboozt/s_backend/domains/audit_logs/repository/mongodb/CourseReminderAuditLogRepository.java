package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb;

import com.talentboozt.s_backend.domains.audit_logs.model.CourseReminderAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl.CourseReminderAuditLogCustomRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReminderAuditLogRepository extends MongoRepository<CourseReminderAuditLog, String>, CourseReminderAuditLogCustomRepo {
    CourseReminderAuditLog findTopByOrderByTimestampDesc();
}

