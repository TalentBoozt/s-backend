package com.talentboozt.s_backend.Repository.COM_JOB_PORTAL;

import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CompanyModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends MongoRepository<CompanyModel, String> {
    Optional<List<CompanyModel>> findAllByCompanyType(String type);
}
