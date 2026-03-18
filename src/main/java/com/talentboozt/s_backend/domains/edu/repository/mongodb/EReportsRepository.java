package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EReports;
import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;
import java.util.List;

@Repository
public interface EReportsRepository extends MongoRepository<EReports, String> {
    List<EReports> findByStatus(EReportStatus status);
}
