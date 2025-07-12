package com.talentboozt.s_backend.shared.scheduler;

import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseReminderLog;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.CourseReminderLogRepository;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import com.talentboozt.s_backend.domains.audit_logs.service.CourseReminderAuditLogService;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRulesException;
import java.util.List;

@Component
public class CourseReminderScheduler {

    @Autowired
    private EmpCoursesRepository empCoursesRepo;

    @Autowired
    private CourseReminderLogRepository reminderLogRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CourseReminderAuditLogService logAuditService;

    @Scheduled(fixedRate = 15 * 60 * 1000) // Every 15 minutes
    public void checkAndSendReminders() {
        Instant now = Instant.now();

        long[] offsets = {60, 1440}; // 60 mins (1h), 1440 mins (24h)

        for (long offset : offsets) {
            Instant targetTime = now.plus(offset, ChronoUnit.MINUTES);
            List<EmpCoursesModel> allEnrolledUsers = empCoursesRepo.findAll();

            for (EmpCoursesModel emp : allEnrolledUsers) {
                if (emp.getCourses() == null) continue;

                for (CourseEnrollment enrollment : emp.getCourses()) {
                    if (enrollment.getModules() == null) continue;

                    for (ModuleDTO module : enrollment.getModules()) {
                        if (module.getUtcStart() == null || module.getMeetingLink() == null) continue;

                        if (emp.getEmail() == null || emp.getTimezone() == null || emp.getEmployeeName() == null) {
                            logAuditService.logAudit(emp, enrollment, module, offset, "SKIPPED", "Missing required fields");
                            continue;
                        }

                        try {
                            Instant moduleStart = Instant.parse(module.getUtcStart());
                            if (Math.abs(Duration.between(moduleStart, targetTime).toMinutes()) <= 10) { // ±10 min tolerance
                                String reminderType = offset == 60 ? "1h" : "24h";

                                boolean alreadySent = reminderLogRepo.existsByEmployeeIdAndModuleIdAndReminderType(
                                        emp.getId(), module.getId(), reminderType
                                );

                                if (!alreadySent) {
                                    sendReminderEmail(emp, enrollment, module, reminderType);
                                    logReminderSent(emp.getId(), enrollment.getCourseId(), module.getId(), reminderType);
                                    logAuditService.logAudit(emp, enrollment, module, offset, "SENT", null);
                                } else {
                                    logAuditService.logAudit(emp, enrollment, module, offset, "SKIPPED", "Already sent");
                                }
                            }
                        } catch (DateTimeParseException | ZoneRulesException | NullPointerException ex) {
                            // Log and skip if malformed date/timezone
                            logAuditService.logAudit(emp, enrollment, module, offset, "FAILED", ex.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void sendReminderEmail(EmpCoursesModel emp, CourseEnrollment course, ModuleDTO module, String reminderType) {
        ZonedDateTime localTime = ZonedDateTime.ofInstant(
                Instant.parse(module.getUtcStart()), ZoneId.of(emp.getTimezone())
        );

        String formattedTime = localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));

        String subject = "Reminder: Upcoming Course Module - " + module.getName();
        String body = "Hi " + emp.getEmployeeName() + ",\n\n"
                + "This is a " + reminderType + " reminder for your upcoming module:\n\n"
                + "Course: " + course.getCourseName() + "\n"
                + "Module: " + module.getName() + "\n"
                + "Start Time: " + formattedTime + "\n"
                + "Meeting Link: " + module.getMeetingLink() + "\n\n"
                + "Good luck and see you there!\n\n"
                + "- Your Course Team";

        emailService.sendSimpleEmail(emp.getEmail(), subject, body);
    }

    private void logReminderSent(String empId, String courseId, String moduleId, String type) {
        CourseReminderLog log = new CourseReminderLog();
        log.setEmployeeId(empId);
        log.setCourseId(courseId);
        log.setModuleId(moduleId);
        log.setReminderType(type);
        log.setSentTime(Instant.now());
        reminderLogRepo.save(log);
    }
}
