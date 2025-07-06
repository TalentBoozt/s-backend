package com.talentboozt.s_backend.Service.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Model.AUDIT_LOGS.TaskRewardAuditModel;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.TaskRewardAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RewardAuditService {

    @Autowired
    private TaskRewardAuditRepository auditRepo;

    public void record(AmbassadorProfileModel ambassador,
                       GamificationTaskModel task,
                       String rewardType,
                       String rewardId,
                       String rewardTitle,
                       String status,
                       String note) {

        TaskRewardAuditModel audit = new TaskRewardAuditModel();
        audit.setAmbassadorId(ambassador.getId());
        audit.setTaskId(task.getId());
        audit.setRewardType(rewardType);
        audit.setRewardId(rewardId);
        audit.setRewardTitle(rewardTitle);
        audit.setStatus(status);
        audit.setNote(note);
        audit.setIssuedAt(Instant.now());

        auditRepo.save(audit);
    }
}
