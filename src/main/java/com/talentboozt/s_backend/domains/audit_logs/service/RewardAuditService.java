package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import com.talentboozt.s_backend.domains.audit_logs.model.TaskRewardAuditModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.TaskRewardAuditRepository;

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
        audit.setExpiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7)); // 7 days expiration

        auditRepo.save(audit);
    }
}
