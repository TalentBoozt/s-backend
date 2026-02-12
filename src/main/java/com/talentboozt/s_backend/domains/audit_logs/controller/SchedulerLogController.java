package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.SchedulerLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/scheduler")
public class SchedulerLogController {

    @Autowired
    private SchedulerLogRepository repo;

    @GetMapping("/{jobName}")
    public List<SchedulerLogModel> getLogs(@PathVariable String jobName) {
        return repo.findByJobNameOrderByRunAtDesc(jobName);
    }

    @GetMapping("/all")
    public List<SchedulerLogModel> getAllLogs() {
        return repo.findAll();
    }

    @GetMapping("/scheduler")
    public Map<String, Object> getSchedulerLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String status
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "runAt"));
        Page<SchedulerLogModel> logs = repo.search(jobName, status, pageable);

        return Map.of(
                "items", logs.getContent(),
                "total", logs.getTotalElements()
        );
    }
}
