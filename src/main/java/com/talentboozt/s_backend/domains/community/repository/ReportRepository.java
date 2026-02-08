package com.talentboozt.s_backend.domains.community.repository;

import com.talentboozt.s_backend.domains.community.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {
}
