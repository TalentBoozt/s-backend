package com.talentboozt.s_backend.Controller.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.SchedulerLogModel;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.SchedulerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
