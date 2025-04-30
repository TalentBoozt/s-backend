package com.talentboozt.s_backend.Repository.common.auth;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CredentialsRepository extends MongoRepository<CredentialsModel, String> {

    CredentialsModel findByEmail(String email);

    Optional<CredentialsModel> findByEmployeeId(String password);

    Optional<CredentialsModel> findByCompanyId(String companyId);

    CredentialsModel deleteByEmployeeId(String employeeIdd);

    boolean existsByEmail(String email);
}
