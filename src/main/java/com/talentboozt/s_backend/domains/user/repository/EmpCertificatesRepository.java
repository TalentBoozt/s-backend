package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.EmpCertificatesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmpCertificatesRepository extends MongoRepository<EmpCertificatesModel, String> {
    List<EmpCertificatesModel> findAllByEmployeeId(String employeeId);
    Optional<EmpCertificatesModel> findByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
}
