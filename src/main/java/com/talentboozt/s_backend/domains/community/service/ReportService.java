package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.Report;
import com.talentboozt.s_backend.domains.community.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public Report createReport(Report report) {
        report.setStatus(Report.ReportStatus.PENDING);
        report.setTimestamp(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public void updateReportStatus(String reportId, Report.ReportStatus status) {
        reportRepository.findById(reportId).ifPresent(r -> {
            r.setStatus(status);
            reportRepository.save(r);
        });
    }
}
