package com.talentboozt.s_backend.domains.community.controller;

import com.talentboozt.s_backend.domains.community.model.Report;
import com.talentboozt.s_backend.domains.community.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public Report submitReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }
}
