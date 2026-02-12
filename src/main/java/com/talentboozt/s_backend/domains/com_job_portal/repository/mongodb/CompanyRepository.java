package com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyModel, String> {
    Optional<List<CompanyModel>> findAllByCompanyType(String type);
}
