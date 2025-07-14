package com.talentboozt.s_backend.domains.audit_logs.repository.Impl;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchedulerLogCustomRepo {
    Page<SchedulerLogModel> search(String jobName, String status, Pageable pageable);
}
