package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.TaskRewardAuditModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRewardAuditCustomRepo {
    Page<TaskRewardAuditModel> search(String ambassadorId, String status, Pageable pageable);
}
