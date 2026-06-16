package com.talentboozt.s_backend.domains.portal.community.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.community.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {
}
