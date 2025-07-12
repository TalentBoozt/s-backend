package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.CourseReminderLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReminderLogRepository extends MongoRepository<CourseReminderLog, String> {
    boolean existsByEmployeeIdAndModuleIdAndReminderType(String employeeId, String moduleId, String reminderType);
}
