package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.audit_logs.model.CourseReminderAuditLog;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.CourseReminderAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CourseReminderAuditLogService {

    private final CourseReminderAuditLogRepository auditLogRepo;

    public CourseReminderAuditLogService(CourseReminderAuditLogRepository auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    public void logAudit(EmpCoursesModel emp, CourseEnrollment course, ModuleDTO module, long offset, String status, String message) {
        CourseReminderAuditLog log = new CourseReminderAuditLog();
        log.setEmployeeId(emp.getEmployeeId());
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
        log.setExpiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 3)); // 3 days expiration
        auditLogRepo.save(log);
    }

}
