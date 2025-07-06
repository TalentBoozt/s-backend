package com.talentboozt.s_backend.Controller.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.TaskRewardAuditModel;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.TaskRewardAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/ambassador/reward-audit")
public class RewardAuditController {

    @Autowired
    private TaskRewardAuditRepository auditRepo;

    @GetMapping("/ambassador/{ambassadorId}")
    public List<TaskRewardAuditModel> getByAmbassador(@PathVariable String ambassadorId) {
        return auditRepo.findByAmbassadorIdOrderByIssuedAtDesc(ambassadorId);
    }

    @GetMapping("/task/{taskId}")
    public List<TaskRewardAuditModel> getByTask(@PathVariable String taskId) {
        return auditRepo.findByTaskIdOrderByIssuedAtDesc(taskId);
    }

    @GetMapping("/rewards")
    public Map<String, Object> getRewardAudits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ambassadorId,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<TaskRewardAuditModel> logs = auditRepo.search(ambassadorId, status, pageable);

        return Map.of(
                "items", logs.getContent(),
                "total", logs.getTotalElements()
        );
    }
}
