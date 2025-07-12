package com.talentboozt.s_backend.Model.AUDIT_LOGS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "course_reminder_audit_logs")
public class CourseReminderAuditLog {

    @Id
    private String id;

    private String employeeId;
    private String employeeName;
    private String email;

    private String courseId;
    private String courseName;

    private String moduleId;
    private String moduleName;

    private String reminderType; // "1h", "24h"
    private String status; // "SENT", "SKIPPED", "FAILED"

    private String timezone;
    private String scheduledStartTime; // ISO string, in employee's local time

    private String message; // Optional note (e.g., "email missing", "invalid timezone")
    private Instant timestamp; // When this log was recorded
}

