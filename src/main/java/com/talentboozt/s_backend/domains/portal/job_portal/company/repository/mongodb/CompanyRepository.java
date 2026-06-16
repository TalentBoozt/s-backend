package com.talentboozt.s_backend.domains.portal.job_portal.company.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.job_portal.company.model.CompanyModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyModel, String> {
    Optional<List<CompanyModel>> findAllByCompanyType(String type);
}
