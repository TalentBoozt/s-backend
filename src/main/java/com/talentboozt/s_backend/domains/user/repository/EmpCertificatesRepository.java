package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.EmpCertificatesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpCertificatesRepository extends MongoRepository<EmpCertificatesModel, String> {
    List<EmpCertificatesModel> findByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
}
