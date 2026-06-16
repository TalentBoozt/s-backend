package com.talentboozt.s_backend.domains.portal.courses.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.platform.model.CourseReminderLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReminderLogRepository extends MongoRepository<CourseReminderLog, String> {
    boolean existsByEmployeeIdAndModuleIdAndReminderType(String employeeId, String moduleId, String reminderType);
}
