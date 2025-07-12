package com.talentboozt.s_backend.Service.AUDIT_LOGS;

import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.DTO.PLAT_COURSES.CourseEnrollment;
import com.talentboozt.s_backend.Model.AUDIT_LOGS.CourseReminderAuditLog;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.CourseReminderAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CourseReminderAuditLogService {

    @Autowired
    private CourseReminderAuditLogRepository auditLogRepo;

    public void logAudit(EmpCoursesModel emp, CourseEnrollment course, ModuleDTO module, long offset, String status, String message) {
        CourseReminderAuditLog log = new CourseReminderAuditLog();
        log.setEmployeeId(emp.getId());
        log.setEmployeeName(emp.getEmployeeName());
        log.setEmail(emp.getEmail());
        log.setTimezone(emp.getTimezone());

        log.setCourseId(course.getCourseId());
        log.setCourseName(course.getCourseName());

        log.setModuleId(module.getId());
        log.setModuleName(module.getName());

        log.setReminderType(offset == 60 ? "1h" : "24h");
        log.setStatus(status);
        log.setMessage(message);

        try {
            ZonedDateTime localTime = ZonedDateTime.ofInstant(
                    Instant.parse(module.getUtcStart()), ZoneId.of(emp.getTimezone())
            );
            log.setScheduledStartTime(localTime.toString());
        } catch (Exception e) {
            log.setScheduledStartTime("Invalid timezone or time");
        }

        log.setTimestamp(Instant.now());
        auditLogRepo.save(log);
    }

}
