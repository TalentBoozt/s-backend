package com.talentboozt.s_backend.shared.async;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.AsyncUpdateAuditLogRepository;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsyncUpdateRetryWorker {

    private static final int MAX_RETRIES = 3;

    @Autowired
    private AsyncUpdateAuditLogRepository auditLogRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseBatchService courseBatchService;

    @Autowired
    private EmpCoursesAsyncUpdater updater;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void retryFailedUpdates() {
        List<AsyncUpdateAuditLog> failedLogs = auditLogRepository.findByStatus("FAILED");

        for (AsyncUpdateAuditLog log : failedLogs) {
            if (log.getRetryCount() >= MAX_RETRIES) continue;

            try {
                CourseModel course = courseService.getCourseById(log.getCourseId());
                CourseBatchModel batch = log.getBatchId() != null
                        ? courseBatchService.getById(log.getBatchId())
                        : null;

                updater.updateEnrolledUsersOnCourseChange(
                        log.getCourseId(), log.getBatchId(), course, batch
                );
            } catch (Exception e) {
                // No need to log again here — updater will re-log it
            }
        }
    }
}
