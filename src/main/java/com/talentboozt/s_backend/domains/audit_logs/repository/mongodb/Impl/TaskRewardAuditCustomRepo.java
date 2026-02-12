package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.TaskRewardAuditModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRewardAuditCustomRepo {
    Page<TaskRewardAuditModel> search(String ambassadorId, String status, Pageable pageable);
}
