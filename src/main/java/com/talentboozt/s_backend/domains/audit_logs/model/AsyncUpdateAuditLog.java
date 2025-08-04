package com.talentboozt.s_backend.domains.audit_logs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "async_update_audit_log")
public class AsyncUpdateAuditLog {

    @Id
    private String id;

    private String courseId;
    private String batchId;
    private String employeeId; // optional

    private String operation; // e.g. "updateCourse", "deleteCourse", etc.
    private String status;    // "PENDING", "SUCCESS", "FAILED"
    private int retryCount;
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expiresAt;
}
