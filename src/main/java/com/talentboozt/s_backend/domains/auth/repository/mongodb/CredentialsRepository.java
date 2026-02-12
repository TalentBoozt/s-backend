package com.talentboozt.s_backend.domains.auth.repository.mongodb;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialsRepository extends MongoRepository<CredentialsModel, String>, CredentialsRepositoryCustom {

    CredentialsModel findByEmail(String email);

    Optional<CredentialsModel> findByEmployeeId(String password);

    Optional<CredentialsModel> findByCompanyId(String companyId);

    CredentialsModel deleteByEmployeeId(String employeeIdd);

    boolean existsByEmail(String email);

    long count();

    long countByDisabledTrue();

    long countByUserLevel(String userLevel);

    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<CredentialsModel> searchByEmail(String email);

    List<CredentialsModel> findAllByEmployeeIdIn(List<String> userIds);
}
