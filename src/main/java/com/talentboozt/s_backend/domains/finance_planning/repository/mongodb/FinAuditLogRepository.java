package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FinAuditLogRepository extends MongoRepository<FinAuditLog, String> {
    List<FinAuditLog> findByProjectIdOrderByTimestampDesc(String projectId);
    List<FinAuditLog> findByOrganizationIdOrderByTimestampDesc(String organizationId);
    List<FinAuditLog> findByOrganizationIdAndProjectId(String organizationId, String projectId);
}
