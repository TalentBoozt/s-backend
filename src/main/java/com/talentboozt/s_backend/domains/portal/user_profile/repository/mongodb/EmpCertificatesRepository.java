package com.talentboozt.s_backend.domains.portal.user_profile.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.user_profile.model.EmpCertificatesModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpCertificatesRepository extends MongoRepository<EmpCertificatesModel, String> {
    List<EmpCertificatesModel> findAllByEmployeeId(String employeeId);

    Optional<EmpCertificatesModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
