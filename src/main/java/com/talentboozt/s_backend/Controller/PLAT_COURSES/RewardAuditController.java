package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.TaskRewardAuditModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.TaskRewardAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
