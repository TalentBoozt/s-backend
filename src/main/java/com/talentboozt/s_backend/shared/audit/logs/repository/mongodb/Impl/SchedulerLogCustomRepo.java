package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb.Impl;

import com.talentboozt.s_backend.shared.audit.logs.model.SchedulerLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchedulerLogCustomRepo {
    Page<SchedulerLogModel> search(String jobName, String status, Pageable pageable);
}
