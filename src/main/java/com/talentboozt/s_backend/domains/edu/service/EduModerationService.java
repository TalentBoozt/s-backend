package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.moderation.ReportRequest;
import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;
import com.talentboozt.s_backend.domains.edu.model.EReports;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EReportsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduModerationService {

    private final EReportsRepository reportsRepository;
    private final ECoursesRepository coursesRepository;

    public EduModerationService(EReportsRepository reportsRepository, ECoursesRepository coursesRepository) {
        this.reportsRepository = reportsRepository;
        this.coursesRepository = coursesRepository;
    }

    public EReports submitReport(ReportRequest request) {
        EReports report = EReports.builder()
                .reporterId(request.getReporterId())
                .targetEntityId(request.getTargetEntityId())
                .entityType(request.getEntityType())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(EReportStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return reportsRepository.save(report);
    }

    public List<EReports> getPendingReports() {
        return reportsRepository.findByStatus(EReportStatus.PENDING);
    }

    public EReports resolveReport(String reportId, String adminId, EReportStatus status, String notes) {
        EReports report = reportsRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        report.setResolutionNotes(notes);
        report.setResolvedBy(adminId);
        report.setUpdatedAt(Instant.now());

        // Example: If an admin explicitly resolves as FRAUD/COPYRIGHT over a course,
        // auto-pull it.
        if (status == EReportStatus.RESOLVED && "COURSE".equalsIgnoreCase(report.getEntityType())) {
            coursesRepository.findById(report.getTargetEntityId()).ifPresent(course -> {
                course.setPublished(false);
                course.setIsPrivate(true);
                // course.setStatus(ECourseStatus.SUSPENDED); // Assuming enum mapped mapping
                // limits
                coursesRepository.save(course);
            });
        }

        return reportsRepository.save(report);
    }
}
