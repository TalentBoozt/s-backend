package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@CompoundIndex(name = "reminder_idx", def = "{'employeeId': 1, 'moduleId': 1, 'reminderType': 1}")
@Document(collection = "course_reminder_logs")
public class CourseReminderLog {
    @Id
    private String id;
    private String employeeId;
    private String moduleId;
    private String courseId;
    private String reminderType; // e.g., "24h", "1h"
    private Instant sentTime;
}

