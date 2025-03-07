package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.CredentialsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CredentialsRepository extends MongoRepository<CredentialsModel, String> {

    CredentialsModel findByEmail(String email);

    Optional<CredentialsModel> findByEmployeeId(String password);

    Optional<CredentialsModel> findByCompanyId(String companyId);

    CredentialsModel deleteByEmployeeId(String employeeIdd);
}
