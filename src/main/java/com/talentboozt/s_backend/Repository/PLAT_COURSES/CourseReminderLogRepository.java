package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseReminderLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReminderLogRepository extends MongoRepository<CourseReminderLog, String> {
    boolean existsByEmployeeIdAndModuleIdAndReminderType(String employeeId, String moduleId, String reminderType);
}
