package com.talentboozt.s_backend.Repository.AUDIT_LOGS.Impl;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.SchedulerLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchedulerLogCustomRepo {
    Page<SchedulerLogModel> search(String jobName, String status, Pageable pageable);
}
