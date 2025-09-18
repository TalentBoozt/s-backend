package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.model.CourseReminderAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.CourseReminderAuditLogRepository;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseReminderAuditLogServiceTest {

    @Mock
    private CourseReminderAuditLogRepository auditLogRepo;

    @InjectMocks
    private CourseReminderAuditLogService courseReminderAuditLogService;

    @Test
    void logAuditSavesLogWithValidData() {
        // Setup input data
        EmpCoursesModel emp = new EmpCoursesModel("1", "emp123", "John Doe", "john.doe@example.com", "0123456789", "Asia/Colombo", null, null);
        CourseEnrollment course = new CourseEnrollment("course123", "Java Basics", "batch123", null, null, null, "2023-10-01T10:00:00Z", "enrolled", null, null, null);
        ModuleDTO module = new ModuleDTO("module123", "Introduction", null, null, null, "2023-10-01", "04:30", "06:00", "2023-10-01T10:00:00Z", "2023-10-01T11:30:00Z", "Asia/Colombo", null, null, null, false);

        // Creating a mock log
        CourseReminderAuditLog log = new CourseReminderAuditLog();
        log.setEmployeeId("emp123");
        log.setCourseId("course123");
        log.setModuleId("module123");
        log.setReminderType("1h");
        log.setStatus("SENT");
        log.setMessage("Reminder sent successfully");
        log.setTimezone("Asia/Colombo");
        log.setEmail("john.doe@example.com");
        log.setEmployeeName("John Doe");
        log.setCourseName("Java Basics");
        log.setModuleName("Introduction");

        // Mock the save method
        when(auditLogRepo.save(any())).thenReturn(log);

        // Call the service method
        courseReminderAuditLogService.logAudit(emp, course, module, 60, "SENT", "Reminder sent successfully");

        // Verify that the save method was called with the correct log
        verify(auditLogRepo).save(argThat(savedLog ->
                savedLog.getCourseId().equals("course123") &&
                        savedLog.getModuleId().equals("module123") &&
                        savedLog.getReminderType().equals("1h") &&
                        savedLog.getStatus().equals("SENT") &&
                        savedLog.getMessage().equals("Reminder sent successfully") &&
                        savedLog.getEmail().equals("john.doe@example.com") &&
                        savedLog.getCourseName().equals("Java Basics") &&
                        savedLog.getModuleName().equals("Introduction") &&
                        savedLog.getEmployeeId().equals("emp123") &&
                        savedLog.getTimezone().equals("Asia/Colombo")
        ));
    }

    @Test
    void logAuditHandlesInvalidTimezoneGracefully() {
        EmpCoursesModel emp = new EmpCoursesModel("1", "emp123", "John Doe", "john.doe@example.com", "0123456789", "InvalidTimezone", null, null);
        CourseEnrollment course = new CourseEnrollment("course123", "Java Basics", "batch123", null, null, null, "2023-10-01T10:00:00Z", "enrolled", null, null, null);
        ModuleDTO module = new ModuleDTO("module123", "Introduction", null, null, null, "2023-10-01", "04:30", "06:00", "2023-10-01T10:00:00Z", "2023-10-01T11:30:00Z", "Asia/Colombo", null, null, null, false);

        courseReminderAuditLogService.logAudit(emp, course, module, 1440, "FAILED", "Invalid timezone");

        verify(auditLogRepo).save(argThat(log ->
                log.getScheduledStartTime().equals("Invalid timezone or time") &&
                        log.getStatus().equals("FAILED") &&
                        log.getMessage().equals("Invalid timezone")
        ));
    }

    @Test
    void logAuditHandlesInvalidUtcStartGracefully() {
        EmpCoursesModel emp = new EmpCoursesModel("1", "emp123", "John Doe", "john.doe@example.com", "0123456789", "Asia/Colombo", null, null);
        CourseEnrollment course = new CourseEnrollment("course123", "Java Basics", "batch123", null, null, null, null, null, null, null, null);
        ModuleDTO module = new ModuleDTO("module123", "Introduction", null, null, null, null, null, null, "InvalidDate", null, null, null, null, null, false);

        courseReminderAuditLogService.logAudit(emp, course, module, 60, "FAILED", "Invalid UTC start time");

        verify(auditLogRepo).save(argThat(log ->
                log.getScheduledStartTime().equals("Invalid timezone or time") &&
                        log.getStatus().equals("FAILED") &&
                        log.getMessage().equals("Invalid UTC start time")
        ));
    }

    @Test
    void logAuditSetsExpirationTimeCorrectly() {
        EmpCoursesModel emp = new EmpCoursesModel("1", "emp123", "John Doe", "john.doe@example.com", "0123456789", "Asia/Colombo", null, null);
        CourseEnrollment course = new CourseEnrollment("course123", "Java Basics", "batch123", null, null, null, null, null, null, null, null);
        ModuleDTO module = new ModuleDTO("module123", "Introduction", null, null, null, null, null, null, "2023-10-01T10:00:00Z", null, null, null, null, null, false);

        courseReminderAuditLogService.logAudit(emp, course, module, 60, "SENT", "Reminder sent successfully");

        verify(auditLogRepo).save(argThat(log ->
                log.getExpiresAt().isAfter(Instant.now()) &&
                        log.getExpiresAt().isBefore(Instant.now().plusSeconds(60 * 60 * 24 * 3 + 1))
        ));
    }
}
